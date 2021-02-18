package physics

import com.badlogic.gdx.physics.box2d.*
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.ashley.remove

class ContactManager: ContactListener {
    private val player: Player by lazy { inject() }
    private val ship: Body by lazy { player.body }

    @ExperimentalStdlibApi
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
            if(contact.hasComponent<EnemySensorComponent>()) {//this is an enemy noticing the player - no system needed
                val enemy = contact.getEntityFor<EnemySensorComponent>()
                val player = contact.getEntityFor<PlayerComponent>()
                enemy.add(SeesPlayerComponent(player.getComponent()))
            }
        }

        if (contact.hasComponent<ShotComponent>()) {
            val entity = contact.getEntityFor<ShotComponent>()
            entity.add(DestroyComponent())
        }
    }

    override fun endContact(contact: Contact) {
        if(contact.isPlayerContact()) {
            if(contact.hasComponent<EnemySensorComponent>()) {
w                val enemy = contact.getEntityFor<EnemySensorComponent>()
                enemy.remove<SeesPlayerComponent>()
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
    }
}