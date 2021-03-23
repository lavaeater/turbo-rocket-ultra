package physics

import com.badlogic.gdx.physics.box2d.*
import ecs.components.*
import ecs.systems.EnemyState
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
                    val enemyComponent = enemy.getComponent<EnemyComponent>()
                val player = contact.getEntityFor<PlayerComponent>()
                enemyComponent.newState(EnemyState.ChasePlayer)
                enemyComponent.chaseTransform = player.getComponent<TransformComponent>()
                enemy.add(PlayerIsInSensorRangeComponent())
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
                val enemy = contact.getEntityFor<EnemySensorComponent>()
                enemy.remove<PlayerIsInSensorRangeComponent>()
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
    }
}