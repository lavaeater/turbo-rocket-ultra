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
import ecs.components.*
import ecs.components.ai.BehaviorComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.*
import ecs.components.graphics.renderables.AnimatedCharacterSprite
import ecs.components.graphics.renderables.RenderableBox
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.player.FiredShotsComponent
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
    const val splatter: Short = 0x0002
    const val player: Short = 0x0003
    const val enemy: Short = 0x0004
    const val objective: Short = 0x0005
    const val obstacle: Short = 0x0006
}

object Box2dCollisionMasks {
    const val splatter = Box2dCategories.splatter
    const val players = Box2dCategories.player
}

fun splatterParticles(
    fromBody: Body,
    towards: Vector2,
    count: Int = 30,
    life: Float = .5f,
    force: ClosedFloatingPointRange<Float> = 1f..10f,
    color: Color = Color.RED
) {
    splatterParticles(fromBody.worldCenter.cpy(), towards, count, life, force, color)
}

fun splatterParticles(
    from: Vector2,
    towards: Vector2,
    count: Int = 1,
    lifeInSeconds: Float = .5f,
    force: ClosedFloatingPointRange<Float> = 1f..10f,
    c: Color = Color(
        colorRange.random(), colorRange.random(), colorRange.random(), 1f
    )
) {


    val splatterAngle = (-1f..1f)
    val forceVector = towards.cpy().scl(force.random())
    forceVector.setAngleDeg((forceVector.angleDeg() + splatterAngle.random()))

    for (i in 0 until count) {
        val box2dBody = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(from)

            circle(0.05f) {
                density = 0.1f
                restitution = 1f
                filter {
                    categoryBits = Box2dCategories.splatter
                    maskBits = Box2dCollisionMasks.splatter
                }
            }
            linearDamping = (25f..125f).random()
            angularDamping = 5f
        }
        val entity = engine().createEntity().apply {
            addComponent<BodyComponent> { body = box2dBody }
            addComponent<TransformComponent> {
                position.set(from)
            }
            addComponent<ParticleComponent> {
                life = lifeInSeconds
                color = c
            }
        }
        box2dBody.userData = entity
        engine().addEntity(entity)
        box2dBody.applyLinearImpulse(
            forceVector,
            box2dBody.worldCenter,
            true
        )
    }
}

fun tower(at: Vector2 = vec2()) {
    val towerBody = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(at)
        box(2f, 2f) {}
    }

    val towerEntity = engine().entity {
        with<BodyComponent> {
            body = towerBody
        }
        with<TransformComponent>()
        with<RenderableComponent>() { renderable = RenderableTextureRegion(Assets.tower) }
        with<RenderLayerComponent>()
        with<TowerComponent>()
    }
    towerEntity.addComponent<BehaviorComponent> { tree = Tree.getTowerBehaviorTree().apply { `object` = towerEntity } }
    towerBody.userData = towerEntity

}

fun player(player: Player, mapper: ControlMapper) {
    val box2dBody = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.setZero()
        circle(1f) {
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
        addComponent<RenderableComponent> { renderable = AnimatedCharacterSprite(Assets.characters[player.selectedCharacterSpriteName]!!) }
        addComponent<RenderLayerComponent>()// { layer = 1 }
        addComponent<PlayerComponent> { this.player = player }
        addComponent<FiredShotsComponent>()
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
        circle(1f) {
            density = GameScreen.ENEMY_DENSITY
            filter {
                categoryBits = Box2dCategories.enemy
            }
        }
        circle(10f) {
            density = .1f
            isSensor = true
        }
    }

    val entity = engine().createEntity().apply {
        addComponent<BodyComponent> { body = box2dBody }
        addComponent<TransformComponent> { position.set(box2dBody.position) }
        addComponent<EnemySensorComponent>()
        addComponent<EnemyComponent>()
        addComponent<RenderableComponent> {
            renderable = AnimatedCharacterSprite(Assets.characters["enemy"]!!) }
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
    width: Float = 2f,
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
        addComponent<RenderableComponent> { renderable = RenderableTextureRegion(Assets.tower) }
        addComponent<RenderLayerComponent>()
    }
    box2dBody.userData = entity
    engine().addEntity(entity)
    return entity
}

fun objective(
    x: Float = (-100f..100f).random(),
    y: Float = (-100f..100f).random(),
    width: Float = 2f,
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
        addComponent<RenderableComponent> { renderable = RenderableTextureRegion(Assets.tower) }
        addComponent<ObjectiveComponent>()
        addComponent<RenderLayerComponent>()
    }
    box2dBody.userData = entity
    engine().addEntity(entity)
    return box2dBody
}

