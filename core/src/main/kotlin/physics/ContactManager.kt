package physics

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.physics.box2d.*
import ecs.components.BodyComponent
import ecs.components.DestroyComponent
import ecs.components.ObstacleComponent
import ecs.components.ShotComponent
import gamestate.Player
import injection.Context
import ktx.ashley.mapperFor

class ContactManager: ContactListener {
    private val player: Player by lazy { Context.inject() }
    private val ship: Body by lazy { player.body }

    override fun beginContact(contact: Contact) {
        //Ship colliding with something
        if (contact.isPlayerContact()) {
            if (contact.hasComponent<ShotComponent>()) {
                //A shot does 20 damage
                player.health -= 20
            }
            if (contact.hasComponent<ObstacleComponent>()) {
                val vel = ship.linearVelocity.len2()
                player.health -= (vel / 15).toInt()
            }
        }

        if (contact.hasComponent<ShotComponent>()) {
            val entity = contact.getEntityFor<ShotComponent>()
            entity.add(DestroyComponent())
        }
    }

    override fun endContact(contact: Contact) {
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
    }
}