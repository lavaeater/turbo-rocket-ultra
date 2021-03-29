package physics

import com.badlogic.gdx.physics.box2d.*
import ai.enemy.EnemyState
import com.badlogic.ashley.core.Engine
import ecs.components.ai.PlayerTrackComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ShotComponent
import ecs.systems.fx.addComponent
import injection.Context.inject

class ContactManager: ContactListener {
    private val engine by lazy {inject<Engine>()}

    @ExperimentalStdlibApi
    override fun beginContact(contact: Contact) {
        //Ship colliding with something
        if (contact.isPlayerContact()) {
            if (contact.hasComponent<ShotComponent>()) {
                //A shot does 20 damage
                contact.getPlayerFor().health -= 20
            }
            if(contact.hasComponent<EnemySensorComponent>()) {//this is an enemy noticing the player - no system needed
                val enemy = contact.getEntityFor<EnemySensorComponent>()
                enemy.add(engine.createComponent(PlayerTrackComponent::class.java).apply { player = contact.getPlayerFor() })
            }
            if(contact.hasComponent<ObjectiveComponent>()) {
                contact.getPlayerFor().touchedObjectives.add(contact.getEntityFor<ObjectiveComponent>().getComponent())
            }
        }

        if(contact.bothHaveComponent<EnemySensorComponent>()) {
            /*
            This is an enemy noticing an enemy - if that enemy is chasing the player, then both should do that!
             */
            val enemyAEntity = contact.fixtureA.getEntity()
            val enemyBEntity = contact.fixtureB.getEntity()
            if(enemyAEntity.hasComponent<PlayerTrackComponent>() && !enemyBEntity.hasComponent<PlayerTrackComponent>()) {
                enemyBEntity.addComponent<PlayerTrackComponent> { player = enemyAEntity.getComponent<PlayerTrackComponent>().player }
            } else if(enemyBEntity.hasComponent<PlayerTrackComponent>() && !enemyAEntity.hasComponent<PlayerTrackComponent>()) {
                enemyAEntity.addComponent<PlayerTrackComponent> { player = enemyBEntity.getComponent<PlayerTrackComponent>().player }
            }
        }

        if (contact.hasComponent<ShotComponent>()) {
            val entity = contact.getEntityFor<ShotComponent>()
            entity.add(DestroyComponent())
        }
    }

    override fun endContact(contact: Contact) {
//        if(contact.isPlayerContact()) {
//            if(contact.hasComponent<EnemySensorComponent>()) {
//                val enemy = contact.getEntityFor<EnemySensorComponent>()
//                enemy.remove<NoticedSomething>()
//            }
//        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
    }
}