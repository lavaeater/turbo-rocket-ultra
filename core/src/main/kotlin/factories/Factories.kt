package factories

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import gamestate.Player
import injection.Context
import injection.Context.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.polygon
import ktx.math.vec2
import tru.FirstScreen

fun world(): World {
    return inject()
}

fun engine(): Engine {
    return inject()
}

fun vehicle(at: Vector2) {
    /*
    Make stuff up and then we change it all later...
     */
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        box(2f, 4f, at, 0f)
    }

    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(VehicleComponent())
    }
    body.userData = entity
    engine().addEntity(entity)
}

fun shot(from: Vector2, towards: Vector2) {
    val world = Context.inject<World>()
    val engine = Context.inject<Engine>()
    val shot = world.body {
        type = BodyDef.BodyType.DynamicBody
        circle(position = from, radius = .5f) {}
    }
    val entity = engine.createEntity().apply {
        add(BodyComponent(shot))
        add(TransformComponent(shot.position))
        add(ShotComponent())
    }
    shot.userData = entity
    shot.linearVelocity = towards.scl(1000f)
    engine.addEntity(entity)
}

fun obstacle(
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 2f,
    height: Float = 2f
) {
    val world = Context.inject<World>()
    val engine = Context.inject<Engine>()
    val body = world.body {
        type = BodyDef.BodyType.StaticBody
        box(width, height, vec2(x, y))
    }
    val entity = engine.createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(ObstacleComponent())
    }
    body.userData = entity
    engine.addEntity(entity)
}

fun player(): Player {
    val world = Context.inject<World>()
    val engine = Context.inject<Engine>()
    val body = world.body {
        type = BodyDef.BodyType.DynamicBody
        polygon(Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(1f, -1f)) {
            density = FirstScreen.SHIP_DENSITY
        }
        linearDamping = FirstScreen.SHIP_LINEAR_DAMPING
        angularDamping = FirstScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine.createEntity().apply {
        add(CameraFollowComponent())
        add(AimComponent())
        add(BodyComponent(body))
        add(TransformComponent())
        add(ControlComponent())
        add(PlayerComponent())
    }

    body.userData = entity

    engine.addEntity(entity)
    return Player(body, entity)
}