package physics

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ecs.components.BodyComponent
import ecs.components.ai.TrackingPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.enemy.TackleComponent
import ecs.components.gameplay.*
import ecs.components.pickups.LootComponent
import ecs.components.player.*
import factories.delayedFireEntity
import factories.fireEntity
import factories.splatterEntity
import factories.world
import features.pickups.AmmoLoot
import features.pickups.WeaponLoot
import injection.Context.inject
import kotlinx.coroutines.delay
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
                val playerEntity = contact.getPlayerFor().entity
                val inventory = playerEntity.getComponent<InventoryComponent>()
                val lootEntity = contact.getEntityFor<LootComponent>()
                val lootComponent = lootEntity.getComponent<LootComponent>()
                var looted = if(lootComponent.lootTable != null) lootComponent.lootTable!!.result else lootComponent.loot
                for(loot in looted) {
                    when (loot) {
                        is AmmoLoot -> {
                            if(!inventory.ammo.containsKey(loot.ammoType))
                                inventory.ammo[loot.ammoType] = 0

                            inventory.ammo[loot.ammoType] = inventory.ammo[loot.ammoType]!! + loot.amount
                        }
                        is WeaponLoot -> {
                            val gun = loot.weaponDefinition.getWeapon()
                            if(!inventory.weapons.any { it.name == gun.name }) {
                                inventory.weapons.add(gun)
                            }
                            if(!playerEntity.has<WeaponComponent>())
                                playerEntity.addComponent<WeaponComponent> {
                                    currentWeapon = gun
                                }
                        }
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
                        engine.createComponent(TrackingPlayer::class.java)
                            .apply { player = contact.getPlayerFor() })
                }
            }
            if(contact.atLeastOneHas<ObjectiveComponent>()) {
                val cEntity = contact.getEntityFor<ObjectiveComponent>()
                val objectiveComponent = cEntity.getComponent<ObjectiveComponent>()
                if(!objectiveComponent.touched)
                    contact.getPlayerFor().touchObjective(objectiveComponent)
                objectiveComponent.touched = true
                cEntity.getComponent<LightComponent>().light.isActive = true
            }
            if(contact.noSensors() && contact.atLeastOneHas<TackleComponent>() ) {
                val enemy = contact.getEntityFor<TackleComponent>()
                val player = contact.getOtherEntity(enemy)
                val playerBody = player.body()
                playerBody.applyLinearImpulse(enemy.getComponent<EnemyComponent>().directionVector.cpy().scl(100f), player.getComponent<TransformComponent>().position, true)
                player.getComponent<PlayerComponent>().player.health -= (5..40).random()
            }
        }

        if(contact.bothHaveComponent<EnemySensorComponent>()) {
            /*
            This is an enemy noticing an enemy - if that enemy is chasing the player, then both should do that!
             */
            val enemyAEntity = contact.fixtureA.getEntity()
            val enemyBEntity = contact.fixtureB.getEntity()
            if(enemyAEntity.has<TrackingPlayer>() && !enemyBEntity.has<TrackingPlayer>()) {
                enemyBEntity.addComponent<TrackingPlayer> { player = enemyAEntity.getComponent<TrackingPlayer>().player }
            } else if(enemyBEntity.has<TrackingPlayer>() && !enemyAEntity.has<TrackingPlayer>()) {
                enemyAEntity.addComponent<TrackingPlayer> { player = enemyBEntity.getComponent<TrackingPlayer>().player }
            }
        }

        if(contact.atLeastOneHas<EnemyComponent>() && contact.atLeastOneHas<BulletComponent>()) {
            val enemy = contact.getEntityFor<EnemyComponent>()
            val enemyComponent = enemy.getComponent<EnemyComponent>()
            val bulletEntity = contact.getEntityFor<BulletComponent>()
            enemyComponent.takeDamage(bulletEntity.getComponent<BulletComponent>().damage)

            val bulletBody = bulletEntity.getComponent<BodyComponent>().body!!
            val splatterAngle = bulletBody.linearVelocity.cpy().angleDeg()

            splatterEntity(
                bulletBody.worldCenter,
                splatterAngle
            )
        }

        if(contact.atLeastOneHas<MolotovComponent>()) {
            /*
            Lets not add a new entity, let's modify the one we have
             */
            val molotov = contact.getEntityFor<MolotovComponent>()
            val body = molotov.getComponent<BodyComponent>().body!!
            val linearVelocity = body.linearVelocity.cpy()
            body.linearVelocity = Vector2.Zero.cpy()

            /*
            Now, add five-ten entities / bodies that fly out in the direction of the molotov
            and also spew fire particles.
             */

            val numOfFireBalls = (5..10).random()
            for(ballIndex in 0..numOfFireBalls) {
                delayedFireEntity(body.worldCenter, linearVelocity)
            }
            molotov.addComponent<DestroyComponent>() //This entity will die and disappear now.
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