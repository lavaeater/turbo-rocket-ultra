package physics

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ShotComponent
import ecs.components.pickups.LootComponent
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerWaitsForRespawn
import features.pickups.AmmoLoot
import injection.Context.inject

class ContactManager: ContactListener {
    private val engine by lazy {inject<Engine>()}

    @ExperimentalStdlibApi
    override fun beginContact(contact: Contact) {
        //Ship colliding with something
        if (contact.isPlayerContact()) {
            if(contact.hasComponent<LootComponent>()) {
                val inventory = contact.getPlayerFor().entity.getComponent<InventoryComponent>()
                val lootEntity = contact.getEntityFor<LootComponent>()
                val lootComponent = lootEntity.getComponent<LootComponent>()
                for(loot in lootComponent.loot) {
                    if(loot is AmmoLoot) {
                        if(!inventory.ammo.containsKey(loot.ammoType))
                            inventory.ammo[loot.ammoType] = 0

                        inventory.ammo[loot.ammoType] = inventory.ammo[loot.ammoType]!! + loot.amount
                    }
                }
                lootEntity.addComponent<DestroyComponent>()
            }
            if (contact.hasComponent<ShotComponent>()) {
                //A shot does 20 damage
                contact.getPlayerFor().health -= 20
            }
            if(contact.hasComponent<EnemySensorComponent>()) {
                //this is an enemy noticing the player - no system needed
                if(!contact.hasComponent<PlayerWaitsForRespawn>()) {
                    val enemy = contact.getEntityFor<EnemySensorComponent>()
                    enemy.add(
                        engine.createComponent(TrackingPlayerComponent::class.java)
                            .apply { player = contact.getPlayerFor() })
                }
            }
            if(contact.hasComponent<ObjectiveComponent>()) {
                val cEntity = contact.getEntityFor<ObjectiveComponent>()
                val objectiveComponent = cEntity.getComponent<ObjectiveComponent>()
                if(!objectiveComponent.touched)
                    contact.getPlayerFor().touchObjective(objectiveComponent)

                //Switch texture, mate!
                //(cEntity.getComponent<RenderableComponent>()?.renderable as RenderableBox)?.color = Color.PURPLE
                objectiveComponent.touched = true
            }
        }

        if(contact.bothHaveComponent<EnemySensorComponent>()) {
            /*
            This is an enemy noticing an enemy - if that enemy is chasing the player, then both should do that!
             */
            val enemyAEntity = contact.fixtureA.getEntity()
            val enemyBEntity = contact.fixtureB.getEntity()
            if(enemyAEntity.hasComponent<TrackingPlayerComponent>() && !enemyBEntity.hasComponent<TrackingPlayerComponent>()) {
                enemyBEntity.addComponent<TrackingPlayerComponent> { player = enemyAEntity.getComponent<TrackingPlayerComponent>().player }
            } else if(enemyBEntity.hasComponent<TrackingPlayerComponent>() && !enemyAEntity.hasComponent<TrackingPlayerComponent>()) {
                enemyAEntity.addComponent<TrackingPlayerComponent> { player = enemyBEntity.getComponent<TrackingPlayerComponent>().player }
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