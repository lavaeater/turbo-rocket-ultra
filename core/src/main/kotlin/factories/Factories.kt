package factories

import ai.Tree
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import data.Player
import ecs.components.BodyComponent
import ecs.components.ai.BehaviorComponent
import ecs.components.ai.GibComponent
import ecs.components.enemy.BossComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.enemy.TackleComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.CameraFollowComponent
import ecs.components.graphics.MiniMapComponent
import ecs.components.graphics.TextureComponent
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ecs.components.towers.TowerComponent
import ecs.systems.graphics.GameConstants.PLAYER_DENSITY
import ecs.systems.graphics.GameConstants.SHIP_ANGULAR_DAMPING
import ecs.systems.graphics.GameConstants.SHIP_LINEAR_DAMPING
import ecs.systems.graphics.GameConstants.pixelsPerMeter
import features.pickups.*
import features.weapons.AmmoType
import features.weapons.Weapon
import features.weapons.WeaponDefinition
import injection.Context.inject
import input.ControlMapper
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.filter
import ktx.math.random
import ktx.math.vec2
import physics.addComponent
import screens.CounterObject
import tru.Assets
import kotlin.experimental.or

fun world(): World {
    return inject()
}

fun engine(): Engine {
    return inject()
}

fun enemy(x: Float = 0f, y: Float = 0f) {
    enemy(vec2(x, y))
}

object Box2dCategories {
    const val none: Short = 0x0000
    const val players: Short = 0x0001
    const val enemies: Short = 0x0002
    const val objectives: Short = 0x0004
    const val obstacles: Short = 0x0008
    const val enemySensors: Short = 0x0010
    const val lights: Short = 0x0020
    const val loot: Short = 0x0040
    const val indicators: Short = 0x0080
    const val bullets: Short = 0x0100
    const val walls: Short = 0x0200
    const val gibs: Short = 0x0400
    const val towerSensors: Short = 0x0800
    val all =
        players or enemies or objectives or obstacles or enemySensors or lights or loot or bullets or walls or gibs
    val allButSensors = players or enemies or objectives or obstacles or lights or loot or bullets or walls or gibs
    val allButLights = players or enemies or objectives or obstacles or enemySensors or loot or bullets or walls or gibs
    val allButLightsOrLoot = players or enemies or objectives or obstacles or enemySensors or bullets or walls or gibs
    val allButLoot = players or enemies or objectives or obstacles or enemySensors or walls or gibs
    val allButLootAndPlayer = enemies or objectives or obstacles or walls or gibs
    val environmentOnly = objectives or obstacles or walls
    val whatGibsHit = players or enemies or walls
    val whatEnemiesHit = players or enemies or objectives or obstacles or walls or lights or bullets or gibs
    val whatPlayersHit =
        players or enemies or objectives or obstacles or walls or lights or gibs or enemySensors or indicators or loot

    /**
     * Will this show up when hovering?
     */
    val thingsBulletsHit = objectives or obstacles or walls or enemies
}

fun gibs(at: Vector2, angle: Float) {
    for (i in Assets.enemyGibs) {
        val angle = (1f..359f).random()
        val force = vec2(40f,0f).setAngleDeg(angle)
        val gibBody = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            box(.3f, .3f) {
                friction = 50f //Tune
                density = 10f //tune
                filter {
                    categoryBits = Box2dCategories.gibs
                    maskBits = Box2dCategories.whatGibsHit
                }
            }
        }
        gibBody.applyLinearImpulse(force, vec2(gibBody.worldCenter.x -0.2f, gibBody.worldCenter.y - 0.2f),true)

        val gibEntity = engine().entity {
            with<TextureComponent> {
                rotateWithTransform = true
                texture = i
            }
            with<TransformComponent> {
                position.set(at)
            }
            with<BodyComponent> {
                body = gibBody
            }
            with<GibComponent> {
                coolDownRange = 1f..3f
                coolDown = coolDownRange.random()
            }
        }
        gibBody.userData = gibEntity
    }
}

fun splatterEntity(at: Vector2, angle: Float) {
    engine().entity {
        with<SplatterComponent> {
            this.at = at.cpy()
            rotation = angle
        }
    }
}

fun tower(at: Vector2 = vec2(), towerType: String = "machinegun") {
    /*
    There should be an abstract "bounds" concept that defines the actual
    width and height of the object (i.e. the sprite). This height and
    width can then be used to create the projection on the floor of the sprite object,
    given a proper anchor etc.
     */
    val towerBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
        box(3f, 1.5f) {}
    }

    val towerEntity = engine().entity {
        with<BodyComponent> {
            body = towerBody
        }
        with<TransformComponent>()
        with<TextureComponent> {
            texture = Assets.towers["obstacle"]!!
            scale = 4f
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<TowerComponent>()
    }
    towerEntity.addComponent<BehaviorComponent> { tree = Tree.getTowerBehaviorTree().apply { `object` = towerEntity } }
    towerBody.userData = towerEntity

}

/**
 * I finally figured it out. I need non-integer heights
 * on all the bodies etc. The sprites are 64 pixels high,
 * we do 16 pixels per meter in other circumstances, that means
 * we have to accomodate this.
 *
 * 64 / 16 = 4, which is weird.
 */

fun bodyForSprite(
    at: Vector2,
    colliderCategoryBits: Short,
    colliderMaskBits: Short,
    detectorBits: Short,
    detectorMaskBits: Short,
    sensorBits: Short,
    sensorMaskBits: Short,
    pixelWidth: Int = 24,
    pixelHeight: Int = 48
): Body {
    val widthInMeters = pixelWidth / pixelsPerMeter
    val heightInMeters = pixelHeight / pixelsPerMeter

    val bottomBoxWidth = widthInMeters
    val bottomBoxHeight = widthInMeters / 2
    val halfHeight = heightInMeters / 2

    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        fixedRotation = true
        //Bottom projection box
        box(bottomBoxWidth, bottomBoxHeight, vec2(0f, halfHeight - bottomBoxHeight / 2)) {
            density = PLAYER_DENSITY
            filter {
                categoryBits = colliderCategoryBits
                maskBits = colliderMaskBits
            }
        }
        box(widthInMeters, heightInMeters, vec2(0f, 0f)) {
            isSensor = true
            filter {
                categoryBits = detectorBits
                maskBits = detectorMaskBits
            }
        }
        circle(2f, vec2(0f, 0f)) {
            isSensor = true
            filter {
                categoryBits = sensorBits
                maskBits = sensorMaskBits
            }
        }
        linearDamping = SHIP_LINEAR_DAMPING
        angularDamping = SHIP_ANGULAR_DAMPING
    }
    return box2dBody
}

fun player(player: Player, mapper: ControlMapper, at: Vector2, pixelWidth: Int = 24, pixelHeight: Int = 48) {
    /*
    The player should be two bodies, one for collision detection for
    movement, like a projection of the characters body on "the floor"
    whereas the other one symbolizes the characters actual body and is for hit detection
    from shots etc. Nice.
     */
    val box2dBody = bodyForSprite(
        at,
        Box2dCategories.players,
        Box2dCategories.whatPlayersHit,
        Box2dCategories.enemySensors,
        Box2dCategories.allButLights,
        Box2dCategories.enemySensors,
        Box2dCategories.allButLights
    )

    val entity = engine().entity() {
        with<CameraFollowComponent>()
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.characters[player.selectedCharacterSpriteName]!!
        }
        with<TextureComponent> {
            layer = 1
            offsetY = -7f
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<PlayerComponent> { this.player = player }
        val weapon = WeaponDefinition.weapons.first().getWeapon()
        with<InventoryComponent> {
//            WeaponDefinition.weapons.forEach {
                weapons.add(WeaponDefinition.molotov.getWeapon())
//            }
        }
        with<WeaponComponent> {
            currentWeapon = weapon

        }
        with<FiredShotsComponent>()
        with<FlashlightComponent>()
        with<WeaponLaserComponent>()
    }
    //TODO: Fix this hot mess
    entity.add(mapper)
    entity.add(PlayerControlComponent(mapper))
    box2dBody.userData = entity

    player.body = box2dBody
    player.entity = entity
}

fun semicircle(): List<Vector2> {
    val radius = 5f
    val vs = mutableListOf<Vector2>()
    vs.add(vec2(0f, 0f))
    for (i in 0 until 7) {
        val angle = (i / 6.0f * 180 * degreesToRadians)
        vs.add(vec2(radius * MathUtils.cos(angle), radius * MathUtils.sin(angle)))
    }
    return vs
}

fun randomLoot(at: Vector2, lootTable: LootTable) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
        fixedRotation = true
        box(1f, 1f) {
            density = 1f
            filter {
                categoryBits = Box2dCategories.loot
                maskBits = Box2dCategories.players or Box2dCategories.lights
            }
        }
        circle(2f) {
            filter {
                categoryBits = Box2dCategories.loot
                maskBits = Box2dCategories.players
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureComponent> {
            texture = Assets.lootBox
            layer = 1
        }
        with<LootComponent> {
            this.lootTable = lootTable
        }
    }
    box2dBody.userData = entity
}

fun lootBox(at: Vector2, lootDrop: List<ILoot>) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
        fixedRotation = true
        box(1f, 1f) {
            density = 1f
            filter {
                categoryBits = Box2dCategories.loot
                maskBits = Box2dCategories.players or Box2dCategories.lights
            }
        }
        circle(2f) {
            filter {
                categoryBits = Box2dCategories.loot
                maskBits = Box2dCategories.players
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureComponent> {
            texture = Assets.lootBox
            layer = 1
        }
        with<LootComponent> {
            loot = lootDrop
        }
    }
    box2dBody.userData = entity
}

fun thrownProjectile(at: Vector2, towards: Vector2, speed: Float) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        linearVelocity.set(towards.cpy().setLength(speed))
        fixedRotation = true
        circle(.2f) {
            density = .1f
            filter {
                categoryBits = Box2dCategories.bullets
                maskBits = Box2dCategories.thingsBulletsHit
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<MolotovComponent> {}
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureComponent> {
            layer = 1
            texture = Assets.bullet //Fix a burning bottle sprite
        }
    }
    box2dBody.userData = entity
    CounterObject.bulletCount++
}

fun bullet(at: Vector2, towards: Vector2, speed: Float, damage: Int) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        linearVelocity.set(towards.cpy().setLength(speed))
        fixedRotation = true
        circle(.2f) {
            density = .1f
            filter {
                categoryBits = Box2dCategories.bullets
                maskBits = Box2dCategories.thingsBulletsHit
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<BulletComponent> {
            this.damage = damage
        }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureComponent> {
            layer = 1
            texture = Assets.bullet
        }
    }
    box2dBody.userData = entity
    CounterObject.bulletCount++
}

fun enemy(at: Vector2) {

    val box2dBody = bodyForSprite(
        at,
        Box2dCategories.enemies,
        Box2dCategories.whatEnemiesHit,
        Box2dCategories.enemies,
        Box2dCategories.bullets,
        Box2dCategories.enemies,
        Box2dCategories.enemies
    )

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<EnemyComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.enemies.values.random()
        }
        with<LootDropComponent> {
            for(weapDef in WeaponDefinition.weapons) {
                lootTable.contents.add(WeaponLoot(weapDef, 5f))
            }
            lootTable.contents.add(
                AmmoLoot(AmmoType.NineMilliMeters, 6..17, 30f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..10, 20f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.FnP90Ammo, 50..150, 10f)
            )
            lootTable.contents.add(
                NullValue(40f)
            )
            //lootTable.contents.add(NullValue(10f))
        }
        with<TextureComponent> {
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.RED
        }
    }
    entity.addComponent<BehaviorComponent> { tree = Tree.getEnemyBehaviorTree().apply { `object` = entity } }
    box2dBody.userData = entity
    CounterObject.enemyCount++
}

fun boss(at: Vector2, level: Int) {

    val box2dBody = bodyForSprite(
        at,
        Box2dCategories.enemies,
        Box2dCategories.all,
        Box2dCategories.enemies,
        Box2dCategories.bullets,
        Box2dCategories.enemySensors,
        Box2dCategories.allButLights,
        64,
        144
    )

//    val box2dBody = world().body {
//        type = BodyDef.BodyType.DynamicBody
//        position.set(at)
//        fixedRotation = true
//        box(3f, 3f) {
//            density = PLAYER_DENSITY
//            filter {
//                categoryBits = Box2dCategories.enemies
//                maskBits = Box2dCategories.all
//            }
//        }
//        box(3f, 6f, vec2(0f, -1.5f)) {
//            filter {
//                categoryBits = Box2dCategories.enemies
//                maskBits = Box2dCategories.bullets
//            }
//        }
//        circle(10f) {
//            density = .1f
//            isSensor = true
//            filter {
//                categoryBits = Box2dCategories.enemySensors
//                maskBits = Box2dCategories.allButLights
//            }
//        }
//    }

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<TackleComponent>()
        with<EnemyComponent> {
            fieldOfView = 270f
            rushSpeed = 15f + level * 1.5f
            viewDistance = 40f + 5f * level
            health = 1000 * level
        }
        with<AnimatedCharacterComponent> {
            anims = Assets.bosses.values.random()
        }
        with<LootDropComponent> {
            lootTable.contents.add(
                AmmoLoot(AmmoType.NineMilliMeters, 6..17, 30f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..10, 20f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.FnP90Ammo, 50..150, 10f)
            )
        }
        with<TextureComponent> {
            scale = 3f
            layer = 1
            offsetY = -7f
        }
        with<MiniMapComponent> {
            color = Color.RED
        }
        with<BossComponent>() {}
    }
    entity.addComponent<BehaviorComponent> { tree = Tree.bossOne().apply { `object` = entity } }
    box2dBody.userData = entity
    CounterObject.enemyCount++
}

fun blockade(
    x: Float,
    y: Float,
    width: Float = 4f,
    height: Float = 4f
) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(x, y)
        box(width, height) {
            restitution = 0f
            filter {
                categoryBits = Box2dCategories.obstacles
            }
        }
    }
    val entity = engine().entity() {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<BlockadeComponent>()
        with<TextureComponent> {
            texture = Assets.buildables.first()
            scale = 4f
            layer = 1
        }
    }
    box2dBody.userData = entity
}

fun obstacle(
    tileX: Float = 0f,
    tileY: Float = 0f,
    width: Float = 4f,
    height: Float = 4f
): Entity {

    val pixelWidth = 64
    val pixelHeight = 64
    val projectedHeight = 64

    val widthInMeters = pixelWidth / pixelsPerMeter
    val projectedHeightInMeters = projectedHeight / pixelsPerMeter

    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(tileX, tileY)
        box(4f,4f,) {//widthInMeters, projectedHeightInMeters, vec2(widthInMeters / 2, -projectedHeightInMeters / 2)) {
            restitution = 0f
            filter {
                categoryBits = Box2dCategories.obstacles
            }
        }
    }
    val entity = engine().entity() {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<ObstacleComponent>()
        with<TextureComponent> {
            texture = Assets.towers["obstacle"]!!
            scale = 4f
//            offsetX = widthInMeters * 2
            offsetY = -4f//-(widthInMeters * 2) - projectedHeightInMeters
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.PINK
        }
    }
    box2dBody.userData = entity
    return entity
}

fun objective(
    x: Float = (-100f..100f).random(),
    y: Float = (-100f..100f).random(),
    width: Float = 4f,
    height: Float = 4f
): Body {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(x, y)
        box(width, height) {
            restitution = 0f
            filter {
                categoryBits = Box2dCategories.objectives
                maskBits = Box2dCategories.players or Box2dCategories.lights
            }
        }
    }
    val entity = engine().entity() {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureComponent> {
            texture = Assets.towers["objective"]!!
            offsetY = -4f
            scale = 4f
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<ObjectiveComponent> {
            id = "I did this"
        }
        with<LightComponent> {
            light.position = box2dBody.position
            light.isStaticLight = true
        }
    }
    box2dBody.userData = entity
    return box2dBody
}
