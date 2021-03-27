package factories

import ai.Tree
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import ecs.components.ai.BehaviorComponent
import gamestate.Player
import injection.Context.inject
import input.ControlMapper
import ktx.box2d.*
import ktx.math.random
import ktx.math.vec2
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

val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.BROWN, Color.CYAN)

val colorRange = 0f..1f

object categories {
    const val splatter : Short = 0x0002
    const val player: Short  = 0x0003
    const val enemy: Short = 0x0004
    const val objective: Short = 0x0005
    const val obstacle: Short = 0x0006
}

object collisionMasks {
    const val splatter = categories.splatter
    const val players = categories.player
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
    life: Float = .5f,
    force: ClosedFloatingPointRange<Float> = 1f..10f,
    color: Color = Color(
        colorRange.random(), colorRange.random(), colorRange.random(), 1f
    )
) {


    val splatterAngle = (-1f..1f)
    val splatRange = -1f..1f
    val localPoint = vec2()
    val pOrC = (0..9)
    val forceVector = towards.cpy().scl(force.random())
    forceVector.setAngleDeg((forceVector.angleDeg() + splatterAngle.random()))

    for (i in 0 until count) {
        val body = world().body {
            type = BodyDef.BodyType.DynamicBody
            position.set(from)

                circle(0.05f) {
                    density = 0.1f
                    restitution = 1f
                    filter {
                        categoryBits = categories.splatter
                        maskBits = collisionMasks.splatter
                    }
                }
            linearDamping = (25f..125f).random()
            angularDamping = 5f
        }
        val entity = engine().createEntity().apply {
            add(BodyComponent(body))
            add(TransformComponent(from))
            add(ParticleComponent(life, color = Color((0.5f..0.7f).random(), 0f, 0f, (.5f..1f).random())))
        }
        body.userData = entity
        engine().addEntity(entity)
        //val applicationVector = body.getWorldPoint(localPoint.set(splatRange.random(), splatRange.random()))
        body.applyLinearImpulse(
forceVector,
            body.worldCenter,
            true
        )
    }
}

fun player(player: Player, controlMapper: ControlMapper) {
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.setZero()
        circle(1f) {
            density = GameScreen.PLAYER_DENSITY
            filter {
                categoryBits = categories.player
            }
        }

        linearDamping = GameScreen.SHIP_LINEAR_DAMPING
        angularDamping = GameScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine().createEntity().apply {
        add(CameraFollowComponent())
        add(BodyComponent(body))
        add(TransformComponent())
        add(controlMapper)
        add(PlayerControlComponent(controlMapper)) //We will have multiple components later
        add(CharacterSpriteComponent(Assets.characters["player"]!!))
        add(RenderableComponent(1))
        add(PlayerComponent(player))
    }

    body.userData = entity

    engine().addEntity(entity)
    player.body = body
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

    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        circle(1f) {
            density = GameScreen.ENEMY_DENSITY
            filter {
                categoryBits = categories.enemy
            }
        }
        circle(10f) {
            density = .1f
            isSensor = true
        }
    }

    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(EnemySensorComponent())
        add(EnemyComponent())
        add(CharacterSpriteComponent(Assets.characters["enemy"]!!))
        add(RenderableComponent(1))
    }
    entity.add(BehaviorComponent(Tree.getEnemyBehaviorTree().apply { `object` = entity}))

    body.userData = entity
    engine().addEntity(entity)
}


fun vehicle(at: Vector2): Body {
    /*
    Make stuff up and then we change it all later...
     */
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        box(2f, 4f) {
            density = GameScreen.CAR_DENSITY
        }
    }

    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(VehicleControlComponent(inject()))
        add(VehicleComponent())
    }
    body.userData = entity
    engine().addEntity(entity)
    return body
}

fun shot(from: Vector2, towards: Vector2): Body {
    val shot = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(from)
        circle(radius = .2f) {
            density = GameScreen.SHOT_DENSITY
        }
    }
    val entity = engine().createEntity().apply {
        add(BodyComponent(shot))
        add(TransformComponent(shot.position))
        add(ShotComponent())
    }
    shot.userData = entity
    shot.linearVelocity = towards.scl(10000f)
    engine().addEntity(entity)
    return shot
}

fun obstacle(
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 2f,
    height: Float = 2f
): Body {
    val body = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(x, y)
        box(width, height) {
            restitution = 0f
            filter {
                categoryBits = categories.obstacle
            }
        }
    }
    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(ObstacleComponent())
        add(BoxComponent(color = Color.BLUE))
        add(RenderableComponent())
    }
    body.userData = entity
    engine().addEntity(entity)
    return body
}

fun objective(
    x: Float = (-100f..100f).random(),
    y: Float = (-100f..100f).random(),
    width: Float = 2f,
    height: Float = 2f
): Body {
    val body = world().body {
        type = BodyDef.BodyType.StaticBody
        position.set(x, y)
        box(width, height) {
            restitution = 0f
            filter {
                categoryBits = categories.objective
                maskBits = collisionMasks.players
            }
        }
    }
    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(BoxComponent())
        add(ObjectiveComponent())
        add(RenderableComponent())
    }
    body.userData = entity
    engine().addEntity(entity)
    return body
}

