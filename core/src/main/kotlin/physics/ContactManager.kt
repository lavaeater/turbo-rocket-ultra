package physics

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ecs.components.BodyComponent
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.gameplay.*
import ecs.components.pickups.LootComponent
import ecs.components.player.*
import factories.splatterEntity
import features.pickups.AmmoLoot
import injection.Context.inject
import ktx.ashley.remove
import tru.Assets

class ContactManager: ContactListener {
    private val engine by lazy {inject<Engine>()}

    @ExperimentalStdlibApi
    override fun beginContact(contact: Contact) {
        if(contact.isPlayerByPlayerContact()) {
            /*
            This means they are close to each other and can do healing and stuff
            Is this dependent on something?
             */
            if(contact.atLeastOneHas<PlayerWaitsForRespawn>()) {
                val deadPlayer = contact.getEntityFor<PlayerWaitsForRespawn>()
                val livingPlayer = contact.getOtherEntity(deadPlayer)
                if(!livingPlayer.has<PlayerWaitsForRespawn>()) {
                    val playerControlComponent = deadPlayer.getComponent<PlayerControlComponent>()
                    val player = deadPlayer.getComponent<PlayerComponent>().player

                    if(livingPlayer.has<ContextActionComponent>()) {
                        livingPlayer.getComponent<ContextActionComponent>().apply {
                            texture = Assets.ps4Buttons["cross"]!!
                            contextAction = {
                                player.health += (50..85).random()
                                deadPlayer.remove<PlayerWaitsForRespawn>()
                                playerControlComponent.waitsForRespawn = false
                            }
                        }
                    } else {
                        livingPlayer.addComponent<ContextActionComponent> {
                            texture = Assets.ps4Buttons["cross"]!!
                            contextAction = {
                                player.health += (50..85).random()
                                deadPlayer.remove<PlayerWaitsForRespawn>()
                                playerControlComponent.waitsForRespawn = false
                            }
                        }
                    }
                }
            }
        }
        if (contact.isPlayerContact()) {
            if(contact.atLeastOneHas<LootComponent>()) {
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
            if (contact.atLeastOneHas<ShotComponent>()) {
                //A shot does 20 damage
                contact.getPlayerFor().health -= 20
            }
            if(contact.atLeastOneHas<EnemySensorComponent>()) {
                //this is an enemy noticing the player - no system needed
                if(!contact.atLeastOneHas<PlayerWaitsForRespawn>() && !contact.atLeastOneHas<PlayerIsRespawning>() ) {
                    val enemy = contact.getEntityFor<EnemySensorComponent>()
                    enemy.add(
                        engine.createComponent(TrackingPlayerComponent::class.java)
                            .apply { player = contact.getPlayerFor() })
                }
            }
            if(contact.atLeastOneHas<ObjectiveComponent>()) {
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
            if(enemyAEntity.has<TrackingPlayerComponent>() && !enemyBEntity.has<TrackingPlayerComponent>()) {
                enemyBEntity.addComponent<TrackingPlayerComponent> { player = enemyAEntity.getComponent<TrackingPlayerComponent>().player }
            } else if(enemyBEntity.has<TrackingPlayerComponent>() && !enemyAEntity.has<TrackingPlayerComponent>()) {
                enemyAEntity.addComponent<TrackingPlayerComponent> { player = enemyBEntity.getComponent<TrackingPlayerComponent>().player }
            }
        }

        if(contact.atLeastOneHas<EnemyComponent>() && contact.atLeastOneHas<BulletComponent>()) {
            val enemy = contact.getEntityFor<EnemyComponent>()
            val enemyComponent = enemy.getComponent<EnemyComponent>()
            val bulletEntity = contact.getEntityFor<BulletComponent>()
            enemyComponent.takeDamage(bulletEntity.getComponent<BulletComponent>().damage)

            val bulletBody = bulletEntity.getComponent<BodyComponent>().body
            val splatterAngle = bulletBody.linearVelocity.cpy().angleDeg()

            splatterEntity(
                bulletBody.worldCenter,
                splatterAngle
            )

        }

        if (contact.atLeastOneHas<BulletComponent>()) {
            val entity = contact.getEntityFor<BulletComponent>()
            entity.add(DestroyComponent())
        }
    }

    override fun endContact(contact: Contact) {
        if(contact.isPlayerByPlayerContact()) {
            /*
            This means they are close to each other and can do healing and stuff
            Is this dependent on something?
             */
            if(contact.atLeastOneHas<ContextActionComponent>()) {
                contact.fixtureA.getEntity().remove<ContextActionComponent>()
                contact.fixtureB.getEntity().remove<ContextActionComponent>()
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
    }
}