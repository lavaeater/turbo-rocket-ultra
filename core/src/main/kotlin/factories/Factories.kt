package factories

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.ashley.get
import ktx.box2d.*
import ktx.math.random
import ktx.math.vec2
import physics.Mappers
import tru.AnimState
import tru.Assets
import tru.FirstScreen

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

//            if (pOrC.random() < 7) {
//                polygon(vec2(0f, 0f), vec2(-.1f, .1f), vec2(.1f, .1f)) {
//                    restitution = 1f
//                    density = .1f
//                }
//            } else {
                circle(0.05f) {
                    density = 0.1f
                    restitution = 1f
                    filter {
                        categoryBits = 0x0002
                        maskBits = 0x0002
                    }
                }
//            }
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

fun player(): Player {
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.setZero()
        circle(1f) {
            density = FirstScreen.PLAYER_DENSITY
        }

        linearDamping = FirstScreen.SHIP_LINEAR_DAMPING
        angularDamping = FirstScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine().createEntity().apply {
        add(CameraFollowComponent())
        add(BodyComponent(body))
        add(TransformComponent())
        add(PlayerControlComponent(inject())) //We will have multiple components later
        add(CharacterSpriteComponent(Assets.characters["player"]!!))
        add(RenderableComponent(1))
        add(PlayerComponent())
    }

    body.userData = entity

    engine().addEntity(entity)
    return Player(body, entity)
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
            density = FirstScreen.ENEMY_DENSITY
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
            density = FirstScreen.CAR_DENSITY
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
            density = FirstScreen.SHOT_DENSITY
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
        }
    }
    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(ObstacleComponent())
    }
    body.userData = entity
    engine().addEntity(entity)
    return body
}

