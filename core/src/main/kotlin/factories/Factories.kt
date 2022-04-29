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
import ecs.components.BodyComponent
import ecs.components.ai.BehaviorComponent
import ecs.components.ai.GibComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.enemy.TackleComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.*
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ecs.components.towers.TowerComponent
import features.pickups.AmmoLoot
import features.pickups.ILoot
import features.pickups.NullValue
import features.weapons.AmmoType
import features.weapons.GunDefinition
import gamestate.Player
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
import screens.GameScreen
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
    val all = players or enemies or objectives or obstacles or enemySensors or lights or loot or bullets or walls or gibs
    val allButSensors = players or enemies or objectives or obstacles or lights or loot or bullets or walls or gibs
    val allButLights = players or enemies or objectives or obstacles or enemySensors or loot or bullets or walls or gibs
    val allButLightsOrLoot = players or enemies or objectives or obstacles or enemySensors or bullets or walls or gibs
    val allButLoot = players or enemies or objectives or obstacles or enemySensors or walls or gibs
    val allButLootAndPlayer = enemies or objectives or obstacles or walls or gibs
    val environmentOnly = objectives or obstacles or walls
    val whatGibsHit = players or enemies or walls
    val whatEnemiesHit = players or enemies or objectives or obstacles or walls or lights or bullets

    /**
     * Will this show up when hovering?
     */
    val thingsBulletsHit = objectives or obstacles or walls or enemies
}

fun gibs(at: Vector2, angle:Float) {
    for(i in Assets.enemyGibs) {
        val angle = (1f..359f).random()
        val velocity = vec2(4f,4f).setAngleDeg(angle)
        val gibBody = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at)
            linearVelocity.set(velocity)
            box( .3f,.3f) {
                friction = 50f //Tune
                density = 10f //tune
                filter {
                    categoryBits = Box2dCategories.gibs
                    maskBits = Box2dCategories.whatGibsHit
                }
            }
        }
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

fun player(player: Player, mapper: ControlMapper, at: Vector2) {
    /*
    The player should be two bodies, one for collision detection for
    movement, like a projection of the characters body on "the floor"
    whereas the other one symbolizes the characters actual body and is for hit detection
    from shots etc. Nice.
     */
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        fixedRotation = true
        box(1f, 1f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = Box2dCategories.players
            }
        }
        box(1f, 2f, vec2(0f, -1.5f)) {
            isSensor = true
            filter {
                categoryBits = Box2dCategories.enemySensors
                maskBits = Box2dCategories.allButLights
            }
        }
        circle(2f, vec2(0f, -1f)) {
            isSensor = true
            filter {
                categoryBits = Box2dCategories.enemySensors
                maskBits = Box2dCategories.allButLights
            }
        }
        linearDamping = GameScreen.SHIP_LINEAR_DAMPING
        angularDamping = GameScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine().entity() {
        with<CameraFollowComponent>()
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.characters[player.selectedCharacterSpriteName]!!
        }
        with<TextureComponent> {
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<PlayerComponent> { this.player = player }
        with<InventoryComponent> {
            GunDefinition.guns.forEach { guns.add(it.getGun()) }
            ammo[AmmoType.nineMilliMeters] = 51
            ammo[AmmoType.twelveGaugeShotgun] = 40
            ammo[AmmoType.fnP90Ammo] = 200
        }
        with<WeaponComponent>()
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


fun bullet(at: Vector2, towards: Vector2, speed:Float, damage: Int) {
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

    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        fixedRotation = true
        box(1f, 1f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = Box2dCategories.enemies
                maskBits = Box2dCategories.whatEnemiesHit
            }
        }
        box(1f, 2f, vec2(0f, -1.5f)) {
            filter {
                categoryBits = Box2dCategories.enemies
                maskBits = Box2dCategories.bullets
            }
        }
        circle(1f, vec2(0f, -2f)) {
            filter {
                categoryBits = Box2dCategories.enemies
                maskBits = Box2dCategories.enemies
            }

        }
        circle(10f) {
            density = .1f
            isSensor = true
            filter {
                categoryBits = Box2dCategories.enemySensors
                maskBits = Box2dCategories.players
            }
        }
    }

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<EnemyComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.enemies.values.random()
        }
        with<LootDropComponent> {
            lootTable.contents.add(
                AmmoLoot(AmmoType.nineMilliMeters, 6..17, 30f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.twelveGaugeShotgun, 4..10, 20f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.fnP90Ammo, 50..150, 10f)
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

    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        fixedRotation = true
        box(3f, 3f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = Box2dCategories.enemies
                maskBits = Box2dCategories.all
            }
        }
        box(3f, 6f, vec2(0f, -1.5f)) {
            filter {
                categoryBits = Box2dCategories.enemies
                maskBits = Box2dCategories.bullets
            }
        }
        circle(10f) {
            density = .1f
            isSensor = true
            filter {
                categoryBits = Box2dCategories.enemySensors
                maskBits = Box2dCategories.allButLights
            }
        }
    }

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<TackleComponent>()
        with<EnemyComponent> {
            fieldOfView = 270f
            rushSpeed = 15f + level * 1.5f
            viewDistance = 40f + 5f* level
            health = 1000 * level
        }
        with<AnimatedCharacterComponent> {
            anims = Assets.bosses.values.random()
        }
        with<LootDropComponent> {
            lootTable.contents.add(
                AmmoLoot(AmmoType.nineMilliMeters, 6..17, 30f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.twelveGaugeShotgun, 4..10, 20f)
            )
            lootTable.contents.add(
                AmmoLoot(AmmoType.fnP90Ammo, 50..150, 10f)
            )
        }
        with<TextureComponent> {
            scale = 3f
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.RED
        }
    }
    entity.addComponent<BehaviorComponent> { tree = Tree.bossOne().apply { `object` = entity } }
    box2dBody.userData = entity
    CounterObject.enemyCount++
}

fun obstacle(
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 4f,
    height: Float = 2f
): Entity {
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
        with<ObstacleComponent>()
        with<TextureComponent> {
            texture = Assets.towers["obstacle"]!!
            scale = 4f
            offsetX = 1.5f
            offsetY = -1f
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
    height: Float = 2f
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
            scale = 4f
            offsetX = 1.5f
            offsetY = -1f
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<ObjectiveComponent>()
        with<LightComponent> {
            light.position = box2dBody.position
            light.isStaticLight = true
        }
    }
    box2dBody.userData = entity
    return box2dBody
}
