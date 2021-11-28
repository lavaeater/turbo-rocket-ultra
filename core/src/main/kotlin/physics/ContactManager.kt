package physics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.*
import ecs.components.pickups.LootComponent
import ecs.components.player.*
import factories.*
import features.pickups.AmmoLoot
import features.pickups.WeaponLoot
import injection.Context.inject
import ktx.ashley.remove
import tru.Assets

/*
How to handle contacts in the absolutely smashingly BEST
way.

perhaps with an extension that does all of this and sets properties
like entity and stuff?

Something that can take parameters and then run some code on realized
objects from those parameters.

Basically what we end up with is a bunch of pairs. If value one evaluates to
true, then we should run the code

But for clarity, it would be nice to have code that evaluates to a ContactType,
because then we can use a nice little when(type)-statement, making the code slightly more readable.

Aha, the sealed classes below can containt properties specific to the type of
contact, which is one of the superpowers of sealed classes, that become available.


 */


sealed class ContactType {
    object Unknown : ContactType()
    class EnemyAndDamage(val damageEntity: Entity, val enemy: Entity) : ContactType()
    class PlayerAndDamage(val damageEntity: Entity, val player: Entity) : ContactType()
    class SomeEntityAndDamage(val damageEntity: Entity, val otherThing: Entity) : ContactType()
    class DamageAndWall(val damageEntity: Entity) : ContactType()
    class PlayerCloseToPlayer(val playerOne: Entity, val playerTwo: Entity) : ContactType()
    class PlayerAndLoot(val player: Entity, val lootEntity: Entity) : ContactType()
    class PlayerAndProjectile(val player: Entity, val shotEntity: Entity) : ContactType()
    class EnemySensesPlayer(val enemy: Entity, val player: Entity) : ContactType()
    class PlayerAndObjective(val player: Entity, val objective: Entity) : ContactType()
    class PlayerAndDeadPlayer(val livingPlayer: Entity, val deadPlayer: Entity) : ContactType()
    class PlayerAndSomeoneWhoTackles(val player: Entity, val tackler: Entity) : ContactType()
    class TwoEnemySensors(val enemyOne: Entity, val enemyTwo: Entity) : ContactType()
    class EnemyAndBullet(val enemy: Entity, val bullet: Entity) : ContactType()
    class MolotovHittingAnything(val molotov: Entity) : ContactType()

}

@ExperimentalStdlibApi
fun Contact.thisIsAContactBetween(): ContactType {
    if (this.justOneHas<DamageEffectComponent>()) {
        val damageEffectEntity = this.getEntityFor<DamageEffectComponent>()
        if(this.bothAreEntities()) {
            val otherEntity = this.getOtherEntity(damageEffectEntity)
            return if (otherEntity.has<PlayerComponent>()) {
                ContactType.PlayerAndDamage(damageEffectEntity, otherEntity)
            } else if (otherEntity.has<EnemyComponent>()) {
                ContactType.EnemyAndDamage(damageEffectEntity, otherEntity)
            } else {
                ContactType.SomeEntityAndDamage(damageEffectEntity, otherEntity)
            }
        } else {
            ContactType.DamageAndWall(damageEffectEntity)
        }
    }

    if (this.isPlayerByPlayerContact()) {
        return if (this.justOneHas<PlayerWaitsForRespawn>()) {
            val deadPlayer = this.getEntityFor<PlayerWaitsForRespawn>()
            val otherPlayer = this.getOtherEntity(deadPlayer)
            ContactType.PlayerAndDeadPlayer(otherPlayer, deadPlayer)
        } else {
            ContactType.PlayerCloseToPlayer(this.fixtureA.getEntity(), this.fixtureB.getEntity())
        }
    }

    if (this.isPlayerContact()) {
        if (this.atLeastOneHas<LootComponent>()) {
            val playerEntity = this.getPlayerFor().entity
            val lootEntity = this.getEntityFor<LootComponent>()

            return ContactType.PlayerAndLoot(playerEntity, lootEntity)
        }
        if (this.atLeastOneHas<ShotComponent>()) {
            val playerEntity = this.getPlayerFor().entity
            return ContactType.PlayerAndProjectile(this.getPlayerFor().entity, this.getOtherEntity(playerEntity))
        }
        if (this.atLeastOneHas<EnemySensorComponent>()) {
            val enemy = this.getEntityFor<EnemySensorComponent>()
            val player = this.getOtherEntity(enemy)
            return ContactType.EnemySensesPlayer(enemy, player)
        }
        if (this.atLeastOneHas<ObjectiveComponent>()) {
            val cEntity = this.getEntityFor<ObjectiveComponent>()
            return ContactType.PlayerAndObjective(this.getPlayerFor().entity, cEntity)
        }
        if (this.noSensors() && this.atLeastOneHas<TackleComponent>()) {
            val enemy = this.getEntityFor<TackleComponent>()
            val player = this.getOtherEntity(enemy)
            return ContactType.PlayerAndSomeoneWhoTackles(player, enemy)
        }
    }

    if (this.bothHaveComponent<EnemySensorComponent>()) {
        /*
        This is an enemy noticing an enemy - if that enemy is chasing the player, then both should do that!
         */
        val enemyAEntity = this.fixtureA.getEntity()
        val enemyBEntity = this.fixtureB.getEntity()
        return ContactType.TwoEnemySensors(enemyAEntity, enemyBEntity)
    }

    if (this.atLeastOneHas<EnemyComponent>() && this.atLeastOneHas<BulletComponent>()) {

        val enemy = this.getEntityFor<EnemyComponent>()
        val bulletEntity = this.getEntityFor<BulletComponent>()
        return ContactType.EnemyAndBullet(enemy, bulletEntity)
    }

    if (this.atLeastOneHas<MolotovComponent>()) {
        /*
        Lets not add a new entity, let's modify the one we have
         */
        val molotov = this.getEntityFor<MolotovComponent>()
        return ContactType.MolotovHittingAnything(molotov)
    }
    return ContactType.Unknown
}


class ContactManager : ContactListener {
    private val engine by lazy { inject<Engine>() }

    @OptIn(ExperimentalStdlibApi::class)
    override fun beginContact(contact: Contact) {
        val contactType = contact.thisIsAContactBetween()
        when (contactType) {
            is ContactType.EnemyAndBullet -> {
                val enemy = contactType.enemy
                val bulletEntity = contactType.bullet

                val enemyComponent = enemy.getComponent<EnemyComponent>()
                val bulletComponent = bulletEntity.getComponent<BulletComponent>()
                enemyComponent.takeDamage(bulletComponent.damage, bulletComponent.player)
                val bulletBody = bulletEntity.getComponent<BodyComponent>().body!!
                val splatterAngle = bulletBody.linearVelocity.cpy().angleDeg()

                enemyComponent.lastShotAngle = splatterAngle

                splatterEntity(
                    bulletBody.worldCenter,
                    splatterAngle
                )
            }
            is ContactType.EnemyAndDamage -> {
                val enemy = contactType.enemy
                enemy.addComponent<BurningComponent> {
                    player = contactType.damageEntity.getComponent<DamageEffectComponent>().player
                }
                enemy.addComponent<ParticleEffectComponent> {
                    effect = Assets.fireEffectPool.obtain()
                }


            }
            is ContactType.EnemySensesPlayer -> {
                val enemy = contactType.enemy
                val playerEntity = contactType.player
                val player = playerEntity.getComponent<PlayerComponent>().player
                if (!contact.atLeastOneHas<PlayerWaitsForRespawn>() && !contact.atLeastOneHas<PlayerIsRespawning>()) {
                    enemy.add(
                        engine.createComponent(TrackingPlayer::class.java)
                            .apply { this.player = player })
                }
            }
            is ContactType.MolotovHittingAnything -> {
                val molotov = contactType.molotov
                val molotovComponent = molotov.getComponent<MolotovComponent>()
                val body = molotov.getComponent<BodyComponent>().body!!
                val linearVelocity = body.linearVelocity.cpy()
                body.linearVelocity = Vector2.Zero.cpy()

                /*
                Now, add five-ten entities / bodies that fly out in the direction of the molotov
                and also spew fire particles.
                 */

                val numOfFireBalls = (10..25).random()
                for (ballIndex in 0..numOfFireBalls) {
                    delayedFireEntity(body.worldCenter, linearVelocity, molotovComponent.player)
                }
                molotov.addComponent<DestroyComponent>() //This entity will die and disappear now.
            }
            is ContactType.SomeEntityAndDamage -> {
                //No op for now
            }
            is ContactType.PlayerAndDamage -> {
                //No op for now
            }
            is ContactType.PlayerAndLoot -> {
                val playerEntity = contactType.player
                val lootEntity = contactType.lootEntity
                val inventory = playerEntity.getComponent<InventoryComponent>()
                val lootComponent = lootEntity.getComponent<LootComponent>()
                val looted =
                    if (lootComponent.lootTable != null) lootComponent.lootTable!!.result else lootComponent.loot
                for (loot in looted) {
                    when (loot) {
                        is AmmoLoot -> {
                            if (!inventory.ammo.containsKey(loot.ammoType))
                                inventory.ammo[loot.ammoType] = 0

                            inventory.ammo[loot.ammoType] = inventory.ammo[loot.ammoType]!! + loot.amount
                        }
                        is WeaponLoot -> {
                            val gun = loot.weaponDefinition.getWeapon()
                            if (!inventory.weapons.any { it.name == gun.name }) {
                                inventory.weapons.add(gun)
                            }
                            if (!playerEntity.has<WeaponComponent>())
                                playerEntity.addComponent<WeaponComponent> {
                                    currentWeapon = gun
                                }
                        }
                    }
                }
                lootEntity.addComponent<DestroyComponent>()
            }
            is ContactType.PlayerAndObjective -> {
                val objectiveComponent = contactType.objective.getComponent<ObjectiveComponent>()
                if (!objectiveComponent.touched)
                    contactType.player.getComponent<PlayerComponent>().player.touchObjective(objectiveComponent)
                objectiveComponent.touched = true
                contactType.objective.getComponent<LightComponent>().light.isActive = true
            }
            is ContactType.PlayerAndProjectile -> {contactType.player.getComponent<PlayerComponent>().player.health -= 20}
            is ContactType.PlayerAndSomeoneWhoTackles -> {
                val enemy = contactType.tackler
                val player = contactType.player
                val playerBody = player.body()
                playerBody.applyLinearImpulse(
                    enemy.getComponent<EnemyComponent>().directionVector.cpy().scl(100f),
                    player.getComponent<TransformComponent>().position,
                    true
                )
                player.getComponent<PlayerComponent>().player.health -= (5..40).random()
            }
            is ContactType.PlayerCloseToPlayer -> {
                //Some other stuff could be done here.
            }
            is ContactType.PlayerAndDeadPlayer -> {
                val deadPlayer = contactType.deadPlayer
                val livingPlayer = contactType.livingPlayer
                val playerControlComponent = deadPlayer.getComponent<PlayerControlComponent>()
                val player = deadPlayer.getComponent<PlayerComponent>().player

                if (livingPlayer.has<ContextActionComponent>()) {
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
            is ContactType.TwoEnemySensors ->  {
                val enemyAEntity = contactType.enemyOne
                val enemyBEntity = contactType.enemyTwo
                if (enemyAEntity.has<TrackingPlayer>() && !enemyBEntity.has<TrackingPlayer>()) {
                    enemyBEntity.addComponent<TrackingPlayer> {
                        player = enemyAEntity.getComponent<TrackingPlayer>().player
                    }
                } else if (enemyBEntity.has<TrackingPlayer>() && !enemyAEntity.has<TrackingPlayer>()) {
                    enemyAEntity.addComponent<TrackingPlayer> {
                        player = enemyBEntity.getComponent<TrackingPlayer>().player
                    }
                }
            }
            ContactType.Unknown -> {

            }
            is ContactType.DamageAndWall -> {
                //No op for now
            }
        }

        if (contact.atLeastOneHas<BulletComponent>()) {
            val entity = contact.getEntityFor<BulletComponent>()
            entity.add(DestroyComponent())
        }
    }

    override fun endContact(contact: Contact) {
        if (contact.isPlayerByPlayerContact()) {
            /*
            This means they are close to each other and can do healing and stuff
            Is this dependent on something?
             */
            if (contact.atLeastOneHas<ContextActionComponent>()) {
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