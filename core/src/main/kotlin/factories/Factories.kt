package factories

import ai.Tree
import ai.behaviors.EnemyBehaviors
import ai.tasks.EntityComponentTask
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Action
import data.Player
import eater.ai.AiComponent
import eater.core.engine
import eater.core.world
import eater.ecs.components.AgentProperties
import eater.ecs.components.Box2d
import eater.ecs.components.TransformComponent
import eater.physics.addComponent
import eater.turbofacts.FactsLikeThatMan
import ecs.components.AudioComponent
import ecs.components.ai.BehaviorComponent
import ecs.components.enemy.*
import ecs.components.fx.CreateEntityComponent
import ecs.components.fx.GibComponent
import ecs.components.fx.ParticleEffectComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.*
import ecs.components.intent.CalculatedPositionComponent
import ecs.components.intent.CalculatedRotationComponent
import ecs.components.intent.FunctionsComponent
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ecs.components.towers.TowerComponent
import ecs.systems.graphics.GameConstants
import ecs.systems.graphics.GameConstants.PIXELS_PER_METER
import ecs.systems.graphics.GameConstants.PLAYER_DENSITY
import ecs.systems.graphics.GameConstants.SHIP_ANGULAR_DAMPING
import ecs.systems.graphics.GameConstants.SHIP_LINEAR_DAMPING
import features.pickups.*
import features.weapons.AmmoType
import features.weapons.WeaponDefinition
import input.ControlMapper
import ktx.actors.plusAssign
import ktx.actors.repeatForever
import ktx.ashley.EngineEntity
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.filter
import ktx.math.random
import ktx.math.vec2
import ktx.scene2d.*
import physics.*
import screens.ApplicationFlags
import screens.CounterObject
import tru.*
import ui.customactors.boundLabel
import ui.getUiThing
import kotlin.experimental.or


fun enemy(x: Float = 0f, y: Float = 0f, choice: Boolean) {
    enemy(vec2(x, y), choice)
}

fun gibs(at: Vector2, gibAngle: Float = 1000f) {
    for (i in Assets.enemyGibs) {
        val angle = if (gibAngle == 1000f) (1f..359f).random() else gibAngle
        val force = vec2((15f..60f).random(), 0f).setAngleDeg(angle + (-25..25).random())
        val gibBody = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(at.x - 2f, at.y - 2f)
            linearDamping = 5f
            box(.3f, .1f) {
                friction = 10f //Tune
                filter {
                    categoryBits = Box2dCategories.gibs
                    maskBits = Box2dCategories.whatGibsHit
                }
            }
        }

        val localPoint = vec2(-1f, 0f)

        val gibEntity = engine().entity {
            with<TextureRegionComponent> {
                rotateWithTransform = true
                textureRegion = i
            }
            with<RenderableComponent> {
            }
            with<TransformComponent> {
                position.set(at)
            }
            with<Box2d> {
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
            this.at.set(at)
            rotation = angle
        }
        with<TransformComponent> {
            position.set(at)
        }
        with<RenderableComponent> {
            renderableType = RenderableType.Effect
            layer = 5
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
        with<RenderableComponent> {
            layer = 5
            renderableType = RenderableType.Effect
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
                categoryBits = Box2dCategories.molotovs
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
        with<Box2d> {
            body = box2dBody
        }
        with<ParticleEffectComponent> {
            effect = Assets.fireEffectPool.obtain()
            rotation = 270f
        }
        with<RenderableComponent> {
            layer = 5
            renderableType = RenderableType.Effect
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
        with<Box2d> {
            body = towerBody
        }
        with<TransformComponent>()
        with<TextureRegionComponent> {
            textureRegion = Assets.newTower
            scale = 4f
//            offsetX = -4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<TowerComponent>()
        with<ObstacleComponent>()
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

fun bodyForRegion(
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
    val widthInMeters = pixelWidth / PIXELS_PER_METER
    val heightInMeters = pixelHeight / PIXELS_PER_METER

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
        circle(2.5f * heightInMeters, vec2(0f, 0f)) {
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
        with<TextureRegionComponent> {
        }
        with<RenderableComponent> {
            layer = 2
            renderableType = RenderableType.TextureRegion
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
    val box2dBody = bodyForRegion(
        at,
        Box2dCategories.players,
        Box2dCategories.whatPlayersHit,
        Box2dCategories.enemySensors,
        Box2dCategories.allButLights,
        Box2dCategories.enemySensors,
        Box2dCategories.allButLights
    )

    player.entity = engine().entity {
        with<AttackableProperties> {
            health = GameConstants.ENEMY_BASE_HEALTH * 10000f
        }
        with<CameraFollowComponent>()
        with<Box2d> { body = box2dBody }
        with<TransformComponent>()
        with<AnimatedCharacterComponent> {
            anims = Assets.characters[player.selectedCharacterSpriteName]!!
            currentAnim = anims.values.first().animations.values.first()
        }
        with<TextureRegionComponent>()
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<PlayerComponent> { this.player = player }
        val weapon = WeaponDefinition.spas12.getWeapon()
        with<InventoryComponent> {
            if (debug) {
                WeaponDefinition.weapons.forEach { weapons.add(it.getWeapon().apply { ammoRemaining = 10000 }) }
                AmmoType.ammoTypes.forEach { ammo[it] = 1000 }
            } else {
                weapons.add(weapon)
                ammo[weapon.ammoType] = 24
            }
        }
        with<WeaponEntityComponent> {
            weaponEntity = playerWeapon(this@entity.entity)
        }
        with<FiredShotsComponent>()
        with<AnchorPointsComponent> {
            points["green"] = vec2(0.5f, 0f)
            points["blue"] = vec2(-0.5f, -0.5f)
            points["red"] = vec2(-0.5f, -0.5f)
            points["yellow"] = vec2(0f, -2f)
            useDirectionVector = true
        }
    }
    player.entity.add(mapper)
    player.entity.add(PlayerControlComponent(mapper, player))
    box2dBody.userData = player.entity

    player.body = box2dBody
    player.isReady = true
//    playerWeapon(entity, "green")
}

fun playerWeapon(playerEntity: Entity, anchor: String = "green"): Entity {
    return engine().entity {
        with<TransformComponent>()
        with<TextureRegionComponent> {
            rotateWithTransform = true
            isVisible = true
            originX = 0f
            originY = 0f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        val weapon = WeaponDefinition.spas12.getWeapon()
        with<WeaponComponent> {
            currentWeapon = weapon
        }
        with<CalculatedPositionComponent> {
            calculate = {
                calcPos.set(playerEntity.anchors().transformedPoints[anchor]!!)
            }
        }
        with<CalculatedRotationComponent> {
            calculate = {
                playerEntity.anchors().transformedPoints[anchor]!!.cpy().sub(playerEntity.transform().position)
                    .angleRad()
            }
        }
        with<FunctionsComponent> {
            functions["SetVisibility"] = { w ->
                w.textureRegionComponent().isVisible = playerEntity.playerControl().aiming
            }
            functions["UpdateWeaponSprite"] = { w ->
                val direction = playerEntity.animation().currentDirection
                w.textureRegionComponent().textureRegion = Assets.weapons.getSpriteFor(w.weapon().currentWeapon, direction)
                when (direction) {
                    CardinalDirection.East -> w.textureRegionComponent().textureRegion.flip(false, false)
                    CardinalDirection.North -> w.textureRegionComponent().textureRegion.flip(false, false)
                    CardinalDirection.South -> w.textureRegionComponent().textureRegion.flip(false, false)
                    CardinalDirection.West -> w.textureRegionComponent().textureRegion.flip(false, true)
                }
            }
        }
    }
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
        box(2f, 2f) {
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
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureRegionComponent> {
            textureRegion = Assets.lootBox
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
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
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureRegionComponent> {
            textureRegion = Assets.lootBox
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
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
        with<Box2d> { body = box2dBody }
        with<GrenadeComponent> {
            this.player = player
        }
        with<TransformComponent> {
            position.set(box2dBody.position)
            feelsGravity = true
            verticalSpeed = 5f
        }
        with<TextureRegionComponent> {
            textureRegion = Assets.molotov //Fix a burning bottle sprite
            rotateWithTransform = true
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
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
        with<Box2d> { body = box2dBody }
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
        with<TextureRegionComponent> {
            textureRegion = Assets.molotov //Fix a burning bottle sprite
            rotateWithTransform = true
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
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
        with<Box2d> { body = box2dBody }
        with<BulletComponent> {
            this.damage = damage
            this.player = player
        }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureRegionComponent> {
            textureRegion = Assets.bullet
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
    }
    box2dBody.userData = entity
    CounterObject.bulletCount++
}

fun enemy(at: Vector2, choice: Boolean, init: EngineEntity.() -> Unit = {}): Entity {
    val box2dBody = bodyForRegion(
        at,
        Box2dCategories.enemies,
        Box2dCategories.whatEnemiesHit,
        Box2dCategories.enemies,
        Box2dCategories.bullets,
        Box2dCategories.enemies,
        Box2dCategories.players
    )
    val entity = engine().entity {
        withBasicEnemyStuff(box2dBody, Assets.enemies.values.random())
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
        if(choice)
            with<TargetComponent>()
        else
            with<AnotherTargetComponent>()
        with<AiComponent> {
            actions.add(EnemyBehaviors.amble)
            actions.add(if(choice) EnemyBehaviors.approachAnotherTarget else EnemyBehaviors.approachTarget)
            actions.add(if(choice) EnemyBehaviors.attackAnotherTarget else EnemyBehaviors.attackTarget)
            actions.add(EnemyBehaviors.panik)
            if (ApplicationFlags.showEnemyActionInfo)
                addActionUiThing(this@entity.entity, this)
        }

        init(this)
    }

    box2dBody.userData = entity
    CounterObject.enemyCount++
    return entity
}

fun oldenemy(at: Vector2, init: EngineEntity.() -> Unit = {}): Entity {

    val box2dBody = bodyForRegion(
        at,
        Box2dCategories.enemies,
        Box2dCategories.whatEnemiesHit,
        Box2dCategories.enemies,
        Box2dCategories.bullets,
        Box2dCategories.enemies,
        Box2dCategories.players
    )
    val entity = engine().entity {
        withBasicEnemyStuff(box2dBody, Assets.enemies.values.random())
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
        with<BehaviorComponent> {
            tree = Tree.nowWithAttacks().apply {
                `object` = this@entity.entity
            }
        }
        init(this)
    }

    box2dBody.userData = entity
    CounterObject.enemyCount++
    return entity
}

fun addActionUiThing(entity: Entity, aiComponent: AiComponent) {
    entity.add(getUiThing {
        val startPosition = hud.worldToHudPosition(entity.transform().position.cpy().add(1f, 1f))

        val moveAction = object : Action() {
            override fun act(delta: Float): Boolean {
                if (entity.hasTransform()) {
                    val coordinate = hud.worldToHudPosition(entity.transform().position.cpy().add(.5f, -.5f))
                    actor.setPosition(coordinate.x, coordinate.y)
                }
                return true
            }
        }.repeatForever()
        stage.actors {
            widget = verticalGroup {
                it += moveAction
                boundLabel({
                    if(AgentProperties.has(entity)) {
                        """${entity.agentProps().directionVector}
${aiComponent.actions.joinToString("\n") { action -> "${action.name}: ${action.score}" }}""".trimMargin()
                    } else ""
                }) {

                }.setPosition(startPosition.x, startPosition.y)
            }
        }
    })
}

fun addUiThing(entity: Entity, bt: BehaviorComponent) {
    entity.add(getUiThing {
        val startPosition = hud.worldToHudPosition(entity.transform().position.cpy().add(1f, 1f))

        val moveAction = object : Action() {
            override fun act(delta: Float): Boolean {
                if (entity.hasTransform()) {
                    val coordinate =
                        hud.worldToHudPosition(entity.transform().position.cpy().add(.5f, -.5f))
                    actor.setPosition(coordinate.x, coordinate.y)
                }
                return true
            }
        }.repeatForever()

        val taskStringList = mutableListOf<String>()
        stage.actors {
            verticalGroup {
                it += moveAction
                label("TreeStatus") { actor ->
                    widget = this
                    bt.tree.addListener(object : BehaviorTree.Listener<Entity> {
                        override fun statusUpdated(task: Task<Entity>, previousStatus: Status) {
                            val taskString = if (task is EntityTask)
                                task.toString()
                            else
                                task::class.simpleName!!
                            taskStringList.add("$taskString - $previousStatus")
                            if (taskStringList.size > 5)
                                taskStringList.removeFirst()

                            this@label.setText(
                                """
                                    ${entity.agentProps().directionVector}
                                    ${taskStringList.joinToString("\n")}
                                    """.trimMargin()
                            )
                        }

                        override fun childAdded(task: Task<Entity>?, index: Int) {

                        }
                    })
                }
            }.setPosition(startPosition.x, startPosition.y)
        }
    })
}

fun targetStation(
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
    val entity = engine().entity {
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TargetComponent>()
        with<AttackableProperties>()
        with<TextureRegionComponent> {
            textureRegion = Assets.towers["objective"]!!
            scale = 4f
//            offsetY = -4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<ObjectiveComponent> {
            id = "I did this"
        }
    }
    box2dBody.userData = entity
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
    val entity = engine().entity {
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<HackingComponent>()
        with<ComplexActionComponent> {
            scene2dTable = scene2d.table {
                label(
                    """
                    Press the key sequence
                    to hack the station""".trimMargin()
                )
                row()
            }
        }
        with<TextureRegionComponent> {
            textureRegion = Assets.towers["objective"]!!
            scale = 4f
//            offsetY = -4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<ObjectiveComponent> {
            id = "I did this"
        }
        with<ObstacleComponent>()
    }
    box2dBody.userData = entity
}

/**
 * The boss entity should take a normal enemy entity and
 * modify it, instead of creating its own from scratch.
 */
fun boss(at: Vector2, level: Int) {

    val box2dBody = bodyForRegion(
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

    lateinit var bt: BehaviorComponent

    val entity = engine().entity {
        withBasicEnemyStuff(
            box2dBody,
            Assets.bosses.values.random(),
            90f,
            10f, 10f,
            20f,
            2000f * level,
            false,
            4f
        )
        with<TackleComponent>()
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
        with<BossComponent> {}

        bt = with {
            tree = Tree.nowWithAttacks().apply { `object` = this@entity.entity }
        }
    }
    box2dBody.userData = entity
    CounterObject.enemyCount++
}

private fun EngineEntity.withBasicEnemyStuff(
    box2dBody: Body,
    anim: Map<AnimState, LpcCharacterAnim<TextureRegion>>,
    fov: Float = GameConstants.ENEMY_FOV,
    rush: Float = GameConstants.ENEMY_RUSH_SPEED,
    velocity: Float = GameConstants.ENEMY_BASE_SPEED,
    howFarCanIsee: Float = GameConstants.ENEMY_VIEW_DISTANCE,
    healthBarValue: Float = GameConstants.ENEMY_BASE_HEALTH,
    isFlocking: Boolean = true,
    spriteScale: Float = 1f
) {
    with<Box2d> { body = box2dBody }
    with<TransformComponent> { position.set(box2dBody.position) }
    with<EnemySensorComponent>()
    with<AudioComponent>()
    with<Enemy>()
    with<AgentProperties> {
        fieldOfView = fov
        rushSpeed = rush
        speed = velocity
        viewDistance = howFarCanIsee
        flock = isFlocking
    }
    with<AttackableProperties> {
        health = healthBarValue
    }
    with<AnimatedCharacterComponent> {
        anims = anim
    }
    with<TextureRegionComponent> {
        scale = spriteScale
    }
    with<Fitness>()
    with<RenderableComponent> {
        layer = 1
        renderableType = RenderableType.TextureRegion
    }
    with<MiniMapComponent> {
        color = Color.RED
    }

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
    val entity = engine().entity {
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<BlockadeComponent>()
        with<ObstacleComponent>()
        with<TextureRegionComponent> {
            textureRegion = Assets.buildables.first()
            scale = 4f
            //offsetY = //-4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
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
        box(
            4f,
            4f
        ) {//widthInMeters, projectedHeightInMeters, vec2(widthInMeters / 2, -projectedHeightInMeters / 2)) {
            restitution = 0f
            filter {
                categoryBits = Box2dCategories.obstacles
            }
        }
    }
    val entity = engine().entity {
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<ObstacleComponent>()
        with<TextureRegionComponent> {
            textureRegion = Assets.towers["obstacle"]!!
//            offsetY = -4f
            scale = 4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<EnemySpawnerComponent> {
            waveSize = FactsLikeThatMan.waveSize
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

    val entity = engine().entity {
        with<Box2d> { body = box2dBody }
        with<TransformComponent> { position.set(box2dBody.position) }
        with<TextureRegionComponent> {
            textureRegion = Assets.towers["objective"]!!
//            offsetY = -4f
            scale = 4f
        }
        with<RenderableComponent> {
            layer = 1
            renderableType = RenderableType.TextureRegion
        }
        with<MiniMapComponent> {
            color = Color.GREEN
        }
        with<ObjectiveComponent> {
            id = "I did this"
        }
        with<ObstacleComponent>()
        if (perimeterObjective)
            with<PerimeterObjectiveComponent>()
    }
    box2dBody.userData = entity
    return box2dBody
}

fun <T> Task<T>.prettyPrint(level: Int = 0): String {
    var aString = if (guard != null) "$level: if ${guard} then - " else "$level: "
    aString += if (this is EntityComponentTask<*> && this.status != Status.FRESH) {
        this.toString()
    } else {
        this::class.simpleName
    }
    for (index in 0 until this.childCount) {
        aString += "\n\t" + this.getChild(index).prettyPrint(level + 1)
    }
    return aString
}
