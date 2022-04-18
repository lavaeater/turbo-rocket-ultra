package factories

import ai.Tree
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree.Listener
import com.badlogic.gdx.ai.btree.Task
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
import ecs.components.enemy.*
import ecs.components.fx.CreateEntityComponent
import ecs.components.fx.ParticleEffectComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.*
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ecs.components.towers.TowerComponent
import ecs.components.AudioComponent
import ecs.systems.graphics.GameConstants.PLAYER_DENSITY
import ecs.systems.graphics.GameConstants.SHIP_ANGULAR_DAMPING
import ecs.systems.graphics.GameConstants.SHIP_LINEAR_DAMPING
import ecs.systems.graphics.GameConstants.pixelsPerMeter
import features.pickups.*
import features.weapons.AmmoType
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
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import physics.addComponent
import screens.CounterObject
import tru.Assets
import ui.getUiThing
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
    const val none: Short = 0
    const val players: Short = 1
    const val enemies: Short = 2
    const val objectives: Short = 4
    const val obstacles: Short = 8
    const val enemySensors: Short = 16
    const val lights: Short = 32
    const val loot: Short = 64
    const val indicators: Short = 128
    const val bullets: Short = 256
    const val walls: Short = 512
    const val gibs: Short = 1024
    const val sensors: Short = 2048
    const val molotov: Short = 4096
    val all =
        players or enemies or objectives or obstacles or enemySensors or lights or loot or bullets or walls or gibs or molotov
    val allButSensors = players or enemies or objectives or obstacles or lights or loot or bullets or walls or gibs
    val allButLights =
        players or enemies or objectives or obstacles or enemySensors or loot or bullets or walls or gibs or molotov
    val whatGibsHit = players or enemies or walls or obstacles or loot or objectives
    val whatEnemiesHit = players or objectives or obstacles or walls or lights or bullets or gibs or sensors
    val whatPlayersHit =
        players or enemies or objectives or obstacles or walls or lights or gibs or enemySensors or indicators or loot
    val whatMolotovsHit = walls or obstacles or objectives or molotov
    val whatSensorsSense = players or enemies

    /**
     * Will this show up when hovering?
     */
    val thingsBulletsHit = objectives or obstacles or walls or enemies
}

fun gibs(at: Vector2, gibAngle: Float = 1000f) {
    for (i in Assets.enemyGibs) {
        val angle = if (gibAngle == 1000f) (1f..359f).random() else gibAngle
        val force = vec2((15f..60f).random(), 0f).setAngleDeg(angle + (-25..25).random())
        val gibBody = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at.x - 2f, at.y - 2f)
            //angularVelocity = 180f * degreesToRadians
            linearDamping = 5f
            box(.3f, .1f) {
                friction = 10f //Tune
                // density = 1f //tune
                filter {
                    categoryBits = Box2dCategories.gibs
                    maskBits = Box2dCategories.whatGibsHit
                }
            }
        }

        val localPoint = vec2(-1f, 0f)

        val gibEntity = engine().entity {
            with<SpriteComponent> {
                rotateWithTransform = true
                sprite = i
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
        gibBody.applyLinearImpulse(force.scl(gibBody.mass), gibBody.getWorldPoint(localPoint).cpy(), true)
        //gibBody.applyAngularImpulse(50f, true)
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

fun delayedFireEntity(at: Vector2, linearVelocity: Vector2, player: Player) {
    engine().entity {
        with<CreateEntityComponent> {
            creator = {
                fireEntity(at, linearVelocity, player)
            }
        }
    }
}

fun explosionEffectEntity(at: Vector2) {
    engine().entity {
        with<TransformComponent> {
            position.set(at)
        }
        with<ParticleEffectComponent> {
            effect = Assets.explosionEffectPool.obtain()
            rotation = 0f
        }
        with<DestroyAfterCoolDownComponent> {
            coolDown = 2f
        }
    }
}

fun fireEntity(at: Vector2, linearVelocity: Vector2, player: Player) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        linearDamping = 5f
        box(.3f, .3f) {
            friction = 10f //Tune
            density = 1f //tune
            restitution = 0.9f
            filter {
                categoryBits = Box2dCategories.molotov
                maskBits = Box2dCategories.whatMolotovsHit
            }
        }
        box(.2f, .5f) {
            isSensor = true
            filter {
                categoryBits = Box2dCategories.sensors
                maskBits = Box2dCategories.whatSensorsSense
            }
        }
    }
    box2dBody.applyLinearImpulse(linearVelocity.scl(box2dBody.mass), box2dBody.getWorldPoint(Vector2.X), true)
    box2dBody.userData = engine().entity {
        with<TransformComponent>()
        with<BodyComponent> {
            body = box2dBody
        }
        with<ParticleEffectComponent> {
            effect = Assets.fireEffectPool.obtain()
            rotation = 270f
        }
        with<DamageEffectComponent> {
            this.player = player
        }
        with<DestroyAfterCoolDownComponent> {
            coolDown = (5f..25f).random()
        }
    }
}

fun tower(
    x: Float,
    y: Float,
    width: Float = 3f,
    height: Float = 3f,
    towerType: String = "machinegun"
) {
    /*
    There should be an abstract "bounds" concept that defines the actual
    width and height of the object (i.e. the sprite). This height and
    width can then be used to create the projection on the floor of the sprite object,
    given a proper anchor etc.
     */
    val towerBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(x, y)
        box(width, height) {}
    }

    val towerEntity = engine().entity {
        with<BodyComponent> {
            body = towerBody
        }
        with<TransformComponent>()
        with<SpriteComponent> {
            sprite = Assets.newTower
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

fun buildCursor(): Entity {
    val entity = engine().entity {
        with<TransformComponent>()
        with<SpriteComponent> {
            layer = 2
        }
    }
    return entity
}

fun player(player: Player, mapper: ControlMapper, at: Vector2, debug: Boolean) {
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
        with<SpriteComponent> {
            layer = 1
//            offsetY = -7f
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<PlayerComponent> { this.player = player }
        val weapon = WeaponDefinition.baseballBat.getWeapon()
        with<InventoryComponent> {
            if (debug) {
                WeaponDefinition.weapons.forEach { weapons.add(it.getWeapon().apply { ammoRemaining = 10000 }) }
                AmmoType.ammoTypes.forEach { ammo[it] = 1000 }
            } else {
                weapons.add(weapon)
            }
        }
        with<WeaponComponent> {
            currentWeapon = weapon
        }
        with<FiredShotsComponent>()
        with<FlashlightComponent>()
        with<WeaponLaserComponent>()
        with<AnchorPointsComponent> {
            points["green"] = vec2(0f, 2f)
            points["melee"] = vec2(-0.5f, -0.5f)
            points["rifle"] = vec2(-0.5f, -0.5f)
            points["yellow"] = vec2(0f, -2f)
            useDirectionVector = true
        }
    }
    entity.add(mapper)
    entity.add(PlayerControlComponent(mapper, player))
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
        with<SpriteComponent> {
            sprite = Assets.lootBox
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
        with<SpriteComponent> {
            sprite = Assets.lootBox
            layer = 1
        }
        with<LootComponent> {
            loot = lootDrop
        }
    }
    box2dBody.userData = entity
}

fun throwGrenade(
    at: Vector2,
    towards: Vector2,
    speed: Float,
    player: Player
) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        linearVelocity.set(towards.cpy().setLength(speed))
        angularVelocity = 180f * degreesToRadians
        box(.5f, .25f) {
            density = .1f
            filter {
                categoryBits = Box2dCategories.bullets
                maskBits = Box2dCategories.thingsBulletsHit
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<GrenadeComponent> {
            this.player = player
        }
        with<TransformComponent> {
            position.set(box2dBody.position)
            feelsGravity = true
            verticalSpeed = 5f
        }
        with<SpriteComponent> {
            layer = 1
            sprite = Assets.molotov //Fix a burning bottle sprite
            rotateWithTransform = true
        }
    }
    box2dBody.userData = entity
    CounterObject.bulletCount++
}

fun throwMolotov(
    at: Vector2,
    towards: Vector2,
    speed: Float,
    player: Player
) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        linearVelocity.set(towards.cpy().setLength(speed))
        angularVelocity = 180f * degreesToRadians
        box(.5f, .25f) {
            density = .1f
            filter {
                categoryBits = Box2dCategories.bullets
                maskBits = Box2dCategories.thingsBulletsHit
            }
        }
    }
    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<MolotovComponent> {
            this.player = player
        }
        with<ParticleEffectComponent> {
            effect = Assets.fireEffectPool.obtain()
        }
        with<TransformComponent> {
            position.set(box2dBody.position)
            feelsGravity = true
            verticalSpeed = 5f
        }
        with<SpriteComponent> {
            layer = 1
            sprite = Assets.molotov //Fix a burning bottle sprite
            rotateWithTransform = true
        }
    }
    box2dBody.userData = entity
    CounterObject.bulletCount++
}

fun bullet(at: Vector2, towards: Vector2, speed: Float, damage: Float, player: Player) {
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
            this.player = player
        }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<SpriteComponent> {
            layer = 1
            sprite = Assets.bullet
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
        Box2dCategories.players
    )

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<AudioComponent>()
        with<EnemyComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.enemies.values.random()
        }
        with<LootDropComponent> {
            WeaponDefinition.weapons.forEach { lootTable.contents.add(WeaponLoot(it, 5f)) }
            lootTable.contents.add(AmmoLoot(AmmoType.NineMilliMeters, 17..51, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.FnP90Ammo, 25..75, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..18, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.Molotov, 1..2, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.Grenade, 1..2, 10f))
            lootTable.contents.add(
                NullValue(200f)
            )
            lootTable.count = (1..5).random()
        }
        with<SpriteComponent> {
            layer = 1
        }
        with<MiniMapComponent> {
            color = Color.RED
        }
    }

    val btComponent =
        entity.addComponent<BehaviorComponent> { tree = Tree.getEnemyBehaviorTree().apply { `object` = entity } }

    val something = getUiThing {
        widget = scene2d.table {
            width = 400f
            height = 400f
            val l = label("TreeStatus")
            btComponent.tree.addListener(object : Listener<Entity> {
                override fun statusUpdated(task: Task<Entity>, previousStatus: Task.Status) {
                    l.setText("$task - $previousStatus")
                }

                override fun childAdded(task: Task<Entity>?, index: Int) {

                }
            })
        }
    }

    box2dBody.userData = entity
    CounterObject.enemyCount++
}

fun hackingStation(
    at: Vector2,
    level: Int,
    width: Float = 4f,
    height: Float = 4f
) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
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
        with<HackingComponent>()
        with<ComplexActionComponent> {
            scene2dTable = scene2d.table {
                label(
                    """
                    Press the key sequence
                    to hack the station""".trimMargin()
                )
            }
        }
        with<SpriteComponent> {
            sprite = Assets.towers["objective"]!!
//            offsetY = -4f
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

    val entity = engine().entity {
        with<BodyComponent> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<EnemySensorComponent>()
        with<TackleComponent>()
        with<EnemyComponent> {
            fieldOfView = 270f
            rushSpeed = 15f + level * 1.5f
            speed = 10f
            viewDistance = 40f + 5f * level
            health = 2000f * level
            flock = false
        }
        with<AnimatedCharacterComponent> {
            anims = Assets.bosses.values.random()
        }
        with<LootDropComponent> {
            WeaponDefinition.weapons.forEach { lootTable.contents.add(WeaponLoot(it, 10f)) }
            lootTable.contents.add(AmmoLoot(AmmoType.NineMilliMeters, 34..96, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.FnP90Ammo, 50..150, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.TwelveGaugeShotgun, 8..36, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.Molotov, 1..4, 10f))
            lootTable.contents.add(AmmoLoot(AmmoType.Grenade, 1..4, 10f))
            lootTable.contents.add(
                NullValue(50f)
            )
            lootTable.count = (3..8).random()
        }
        with<SpriteComponent> {
            layer = 1
            scale = 4f

//            offsetY = -7f
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
        with<SpriteComponent> {
            sprite = Assets.buildables.first()
            scale = 4f
            layer = 1
        }
    }
    box2dBody.userData = entity
}

fun spawner(
    tileX: Float = 0f,
    tileY: Float = 0f
): Entity {
    val box2dBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(tileX, tileY)
        box(4f, 4f) {//widthInMeters, projectedHeightInMeters, vec2(widthInMeters / 2, -projectedHeightInMeters / 2)) {
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
        with<SpriteComponent> {
            sprite = Assets.towers["obstacle"]!!
//            offsetY = -4f
            scale = 4f
            layer = 1
        }
        with<EnemySpawnerComponent> {
            waveSize = 1
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
    perimeterObjective: Boolean,
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
        with<SpriteComponent> {
            sprite = Assets.towers["objective"]!!
//            offsetY = -4f
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
        if (perimeterObjective)
            with<PerimeterObjectiveComponent>()
    }
    box2dBody.userData = entity
    return box2dBody
}
