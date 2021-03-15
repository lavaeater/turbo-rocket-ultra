package factories

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.polygon
import ktx.math.vec2
import tru.Assets
import tru.FirstScreen

fun world(): World {
    return inject()
}

fun engine(): Engine {
    return inject()
}

/**
 * Enemies, what are we doing this time?
 *
 * Well, the enemy is looking for players.
 *
 * So, the enemy has some kind of sensor component
 *
 * So the enemy could have a couple of states or something.
 *
 * This is quite obviously a decision tree. We should make
 * a DSL for decision trees, that would be cool. We could probably really
 * quickly construct a decision tree implementation quite simply.
 *
 * Anyways, the enemy is either:
 * - looking for the player
 * - chasing the player
 * - attacking the player
 *
 * Looking for the player
 *
 * How does an enemy look for the player?
 *
 * If the enemy has seen the player before, but no longer can see it, the enemy
 * should move towards the last known position, and then do something else there.
 *
 * This can all be handled with a decision tree.
 * Avoiding obstacles?
 *
 * If the enemy doesn't have knowledge of the players position...
 *
 * We need to quickly build a decision tree that can control an enemy thingamajig.
 *
 */
fun enemy(x:Float = 0f, y: Float = 0f) {
    enemy(vec2(x,y))
}

fun enemy(at: Vector2) {
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.set(at)
        circle(1f) {
            density = FirstScreen.ENEMY_DENSITY
        }
        circle(3f, vec2(1f, 0f)) {
            density = 0.01f
            isSensor = true
        }
    }

    val entity = engine().createEntity().apply {
        add(BodyComponent(body))
        add(TransformComponent(body.position))
        add(EnemySensorComponent())
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

fun shot(from: Vector2, towards: Vector2) :Body {
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
) : Body {
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

fun player(): Player {
    val body = world().body {
        type = BodyDef.BodyType.DynamicBody
        position.setZero()
        circle(0.25f) {
            density = FirstScreen.PLAYER_DENSITY
        }

        linearDamping = FirstScreen.SHIP_LINEAR_DAMPING
        angularDamping = FirstScreen.SHIP_ANGULAR_DAMPING
    }

    val entity = engine().createEntity().apply {
        add(CameraFollowComponent())
        add(AimComponent())
        add(BodyComponent(body))
        add(TransformComponent())
        add(PlayerControlComponent(inject())) //We will have multiple components later
        add(CharacterSpriteComponent(Assets.characters["player"]!!))
        add(PlayerComponent())
    }

    body.userData = entity

    engine().addEntity(entity)
    return Player(body, entity)
}