package factories

import ai.Tree
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import ecs.components.ai.BehaviorComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.*
import ecs.components.graphics.renderables.AnimatedCharacterSprite
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.player.FiredShotsComponent
import ecs.components.player.FlashlightComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.towers.TowerComponent
import gamestate.Player
import injection.Context.inject
import input.ControlMapper
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.*
import ktx.math.random
import ktx.math.vec2
import physics.addComponent
import tru.Assets
import screens.GameScreen
import kotlin.experimental.or
import kotlin.experimental.xor

fun world(): World {
    return inject()
}

fun engine(): Engine {
    return inject()
}

fun enemy(x: Float = 0f, y: Float = 0f) {
    enemy(vec2(x, y))
}

val colorRange = 0f..1f

object Box2dCategories {
    const val player: Short = 0x01
    const val enemy: Short = 0x02
    const val objective: Short = 0x04
    const val obstacle: Short = 0x08
    const val sensor: Short = 0x10
    const val light: Short = 0x20
    val all = player or enemy or objective or obstacle or sensor or light
    val allButSensors = player or enemy or objective or obstacle or light
    val allButLights = player or enemy or objective or obstacle or sensor
}

object Box2dCollisionMasks {
    const val players = Box2dCategories.player
}

fun splatterEntity(at: Vector2, angle: Float) {
    val splatterEntity = engine().entity {
        with<SplatterComponent> {
            this.at = at.cpy()
            this.rotation = angle
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
        with<RenderableComponent> { renderable = RenderableTextureRegion(Assets.towers[towerType]!!, 4f, 0f, -5f) }
        with<RenderLayerComponent>()
        with<TowerComponent>()
    }
    towerEntity.addComponent<BehaviorComponent> { tree = Tree.getTowerBehaviorTree().apply { `object` = towerEntity } }
    towerBody.userData = towerEntity

}

fun player(player: Player, mapper: ControlMapper,at: Vector2) {
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
        box(2f, 1f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = Box2dCategories.player
            }
        }
        linearDamping = GameScreen.SHIP_LINEAR_DAMPING
        angularDamping = GameScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine().createEntity().apply {
        addComponent<CameraFollowComponent>()
        addComponent<BodyComponent> { body = box2dBody }
        addComponent<TransformComponent>()
        add(mapper)
        add(PlayerControlComponent(mapper))
        addComponent<RenderableComponent> {
            renderable = AnimatedCharacterSprite(
                Assets.characters[player.selectedCharacterSpriteName]!!,
                1f,
                0f, -20f
            )
        }
        addComponent<RenderLayerComponent>()// { layer = 1 }
        addComponent<PlayerComponent> { this.player = player }
        addComponent<FiredShotsComponent>()
        addComponent<FlashlightComponent>()
    }

    box2dBody.userData = entity

    engine().addEntity(entity)
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

fun enemy(at: Vector2) {

    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        fixedRotation = true
        box(2f, 1f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = Box2dCategories.enemy
                maskBits = Box2dCategories.all
            }
        }
        circle(10f) {
            density = .1f
            isSensor = true
            filter {
                categoryBits = Box2dCategories.sensor
                maskBits = Box2dCategories.allButLights
            }
        }
    }

    val entity = engine().createEntity().apply {
        addComponent<BodyComponent> { body = box2dBody }
        addComponent<TransformComponent> { position.set(box2dBody.position) }
        addComponent<EnemySensorComponent>()
        addComponent<EnemyComponent>()
        addComponent<RenderableComponent> {
            renderable = AnimatedCharacterSprite(
                Assets.characters["enemy"]!!,
                1f,
                0f, -20f
            )
        }
        addComponent<RenderLayerComponent>()// { layer = 1 }
    }
    entity.addComponent<BehaviorComponent> { tree = Tree.getEnemyBehaviorTree().apply { `object` = entity } }

    box2dBody.userData = entity
    engine().addEntity(entity)
}


//fun vehicle(at: Vector2): Body {
//    /*
//    Make stuff up and then we change it all later...
//     */
//    val body = world().body {
//        type = BodyDef.BodyType.DynamicBody
//        position.set(at)
//        box(2f, 4f) {
//            density = GameScreen.CAR_DENSITY
//        }
//    }
//
//    val entity = engine().createEntity().apply {
//        add(BodyComponent(body))
//        add(TransformComponent(body.position))
//        add(VehicleControlComponent(inject()))
//        add(VehicleComponent())
//    }
//    body.userData = entity
//    engine().addEntity(entity)
//    return body
//}

//fun shot(from: Vector2, towards: Vector2): Body {
//    val shot = world().body {
//        type = BodyDef.BodyType.DynamicBody
//        position.set(from)
//        circle(radius = .2f) {
//            density = GameScreen.SHOT_DENSITY
//        }
//    }
//    val entity = engine().createEntity().apply {
//        add(BodyComponent(shot))
//        add(TransformComponent(shot.position))
//        add(ShotComponent())
//    }
//    shot.userData = entity
//    shot.linearVelocity = towards.scl(10000f)
//    engine().addEntity(entity)
//    return shot
//}

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
                categoryBits = Box2dCategories.obstacle
            }
        }
    }
    val entity = engine().createEntity().apply {
        addComponent<BodyComponent> { body = box2dBody }
        addComponent<TransformComponent> { position.set(box2dBody.position) }
        addComponent<ObstacleComponent>()
        addComponent<RenderableComponent> {
            renderable = RenderableTextureRegion(Assets.towers["obstacle"]!!, 4f, 0f, -6f)
        }
        addComponent<RenderLayerComponent>()
    }
    box2dBody.userData = entity
    engine().addEntity(entity)
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
                categoryBits = Box2dCategories.objective
                maskBits = Box2dCollisionMasks.players
            }
        }
    }
    val entity = engine().createEntity().apply {
        addComponent<BodyComponent> { body = box2dBody }
        addComponent<TransformComponent> { position.set(box2dBody.position) }
        addComponent<RenderableComponent> {
            renderable = RenderableTextureRegion(Assets.towers["objective"]!!, 4f, 0f, -6f)
        }
        addComponent<ObjectiveComponent>()
        addComponent<RenderLayerComponent>()
    }
    box2dBody.userData = entity
    engine().addEntity(entity)
    return box2dBody
}

