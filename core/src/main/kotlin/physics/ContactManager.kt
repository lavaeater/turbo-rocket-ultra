package physics

import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import ecs.components.AudioChannels
import ecs.components.BodyComponent
import ecs.components.ai.AttackPoint
import ecs.components.ai.CollidedWithObstacle
import ecs.components.enemy.AgentProperties
import ecs.components.enemy.AttackableProperties
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.*
import ecs.components.pickups.LootComponent
import ecs.components.player.*
import factories.delayedFireEntity
import factories.engine
import factories.explosionEffectEntity
import factories.splatterEntity
import features.pickups.AmmoLoot
import features.pickups.WeaponLoot
import injection.Context.inject
import input.Button
import ktx.ashley.allOf
import ktx.ashley.remove
import ktx.math.random
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.table
import messaging.Message
import messaging.MessageHandler
import screens.CounterObject
import tru.Assets
import kotlin.math.pow
import kotlin.math.roundToInt

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

fun Int.pow(exponent: Int): Int {
    return this.toDouble().pow(exponent).roundToInt()
}

object InputSequenceGenerator {

    private val gamePadKeys = listOf(Button.DPadUp, Button.DPadDown, Button.DPadLeft, Button.DPadRight)
    private val keyboardKeys = listOf(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT)

    fun generate(gamepad: Boolean, level: Int): List<Int> {
        val numberOfThings = 2.pow(level)
        return if (gamepad)
            Array(numberOfThings) {
                Button.buttonsToCodes[gamePadKeys.random()]!!
            }.toList()
        else
            Array(numberOfThings) {
                keyboardKeys.random()
            }.toList()
    }
}

class ContactManager : ContactListener {
    private val engine by lazy { inject<Engine>() }
    private val messageHandler by lazy { inject<MessageHandler>() }
    private val audioPlayer by lazy { inject<AudioPlayer>() }

    override fun beginContact(contact: Contact) {
        when (val contactType = contact.thisIsAContactBetween()) {
            is ContactType.EnemyAndBullet -> {
                val enemyEntity = contactType.enemy
                val bulletEntity = contactType.bullet

                enemyEntity.fitnessDown()

                val enemyComponent = enemyEntity.agentProps()
                val ap = enemyEntity.getComponent<AttackableProperties>()
                val bulletComponent = bulletEntity.bullet()
                ap.takeDamage(bulletComponent.damage, bulletComponent.player.entity)
                val bulletBody = bulletEntity.body()
                val splatterAngle = bulletBody.linearVelocity.cpy().angleDeg()

                enemyComponent.lastShotAngle = splatterAngle

                splatterEntity(
                    bulletBody.worldCenter,
                    splatterAngle
                )
            }
            is ContactType.EnemyAndDamage -> {
                val enemy = contactType.enemy
                enemy.fitnessDown()

                enemy.addComponent<BurningComponent> {
                    player = contactType.damageEntity.getComponent<DamageEffectComponent>().player.entity
                }
                enemy.addComponent<ParticleEffectComponent> {
                    effect = Assets.fireEffectPool.obtain()
                }


            }
            is ContactType.EnemySensesPlayer -> {
                val enemy = contactType.enemy
                enemy.fitnessUp()

                val playerEntity = contactType.player
                val player = playerEntity.getComponent<PlayerComponent>().player
                if (!contact.atLeastOneHas<PlayerWaitsForRespawn>() && !contact.atLeastOneHas<PlayerIsRespawning>()) {
                    enemy.add(
                        engine.createComponent(AttackPoint::class.java)
                            .apply { this.position = playerEntity.transform().position })
                }
            }
            is ContactType.MolotovHittingAnything -> {
                handleMolotovHittingAnything(contactType)
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
                val lootPosition = lootEntity.getComponent<TransformComponent>().position
                audioPlayer.playOnChannel(playerEntity.playerControl().player.playerId, "players", "loot-found")

                val looted =
                    if (lootComponent.lootTable != null) lootComponent.lootTable!!.result else lootComponent.loot
                for (loot in looted) {
                    messageHandler.sendMessage(Message.ShowToast(loot.toString(), lootPosition.cpy()))
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
                                if(!inventory.ammo.containsKey(gun.ammoType))
                                    inventory.ammo[gun.ammoType] = 0
                            }
                            if (!playerEntity.has<WeaponComponent>())
                                playerEntity.addComponent<WeaponComponent> {
                                    currentWeapon = gun
                                }
                        }
                    }
                }
                lootEntity.safeDestroy()
            }
            is ContactType.PlayerAndObjective -> {
                val objectiveComponent = contactType.objective.getComponent<ObjectiveComponent>()
                if (!objectiveComponent.touched)
                    contactType.player.getComponent<PlayerComponent>().player.touchObjective(objectiveComponent)
                objectiveComponent.touched = true
                contactType.objective.getComponent<LightComponent>().light.isActive = true
            }
            is ContactType.PlayerAndProjectile -> {
                contactType.player.getComponent<AttackableProperties>().health -= 20
            }
            is ContactType.PlayerAndSomeoneWhoTackles -> {
                val enemy = contactType.tackler
                enemy.fitnessUp()
                val player = contactType.player

                val playerComponent = contactType.player.playerControl()

                playerComponent.startCooldown(playerComponent::stunned, 1f)

                val playerBody = player.body()
                playerBody.applyLinearImpulse(
                    enemy.getComponent<AgentProperties>().directionVector.cpy().scl(100f),
                    player.getComponent<TransformComponent>().position,
                    true
                )
                player.getComponent<AttackableProperties>().health -= (5..40).random()
            }
            is ContactType.PlayerCloseToPlayer -> {
                //Some other stuff could be done here.
            }
            is ContactType.PlayerAndDeadPlayer -> {
                val deadPlayer = contactType.deadPlayer
                val livingPlayer = contactType.livingPlayer
                val playerControlComponent = deadPlayer.getComponent<PlayerControlComponent>()
                val player = deadPlayer.getComponent<PlayerComponent>().player

                if (livingPlayer.hasContextAction()) {
                    livingPlayer.contextAction().apply {
                        sprite = Assets.ps4Buttons["cross"]!!
                        contextAction = {
                            player.entity.getComponent<AttackableProperties>().health += (50..85).random()
                            deadPlayer.remove<PlayerWaitsForRespawn>()
                            playerControlComponent.waitsForRespawn = false
                        }
                    }
                } else {
                    livingPlayer.addContextAction {
                        sprite = Assets.ps4Buttons["cross"]!!
                        contextAction = {
                            player.entity.getComponent<AttackableProperties>().health += (50..85).random()
                            deadPlayer.remove<PlayerWaitsForRespawn>()
                            playerControlComponent.waitsForRespawn = false
                        }
                    }
                }
            }
            is ContactType.EnemyAndEnemy -> {
                /*
                This actually kinda works for the new behavior, this is the new information sent to this particular enemy

                 */

                val enemyAEntity = contactType.enemyOne
                val enemyBEntity = contactType.enemyTwo

            }
            ContactType.Unknown -> {

            }
            is ContactType.DamageAndWall -> {
                //No op for now
            }
            is ContactType.PlayerAndComplexAction -> {
                val playerControl = contactType.player.playerControl()
                val playerPosition = contactType.player.transform().position
                val complexActionComponent = contactType.other.complexAction()
                val objectiveComponent = contactType.other.objective()
                if (!complexActionComponent.busy) {
                    playerControl.locked = true
                    if (contactType.other.hasHacking()) {
                        //create the done function, I suppose?
                        val inputSequence: List<Int> = InputSequenceGenerator.generate(
                            playerControl.controlMapper.isGamepad,
                            CounterObject.currentLevel
                        )
                        playerControl.requireSequence(inputSequence)
                        complexActionComponent.scene2dTable.table {
                            if (playerControl.controlMapper.isGamepad) {
                                label(inputSequence.map { Button.getButton(it).playstationButtonName }
                                    .joinToString(" "))
                                //row()
                                inputSequence.map { this.add(image(Button.getButton(it).image).apply { scaleBy(1.25f) }) }
                            } else {
                                label(inputSequence.map { Input.Keys.toString(it) }.joinToString(" "))
                            }
                            pack()
                        }
                        complexActionComponent.doneFunction = {
                            playerControl.sequencePressingProgress()
                        }
                        complexActionComponent.doneCallBacks.add {
                            playerControl.locked = false
                            complexActionComponent.busy = false
                            if (it == ComplexActionResult.Success) {
                                objectiveComponent.touched = true
                                contactType.other.getComponent<LightComponent>().light.isActive = true
                                playerControl.player.touchObjective(objectiveComponent)
                            }
                        }
                    }
                    complexActionComponent.busy = true
                    messageHandler.sendMessage(
                        Message.ShowUiForComplexAction(
                            complexActionComponent,
                            playerControl,
                            playerPosition
                        )
                    )
                }
            }
            is ContactType.GrenadeHittingAnything -> {
                handleGrenadeHittingAnything(contactType)
            }
            is ContactType.EnemyAndObstacle -> {
                handleEnemyHittingObstacle(contactType)
            }
        }

        if (contact.atLeastOneHas<BulletComponent>()) {
            val entity = contact.getEntityFor<BulletComponent>()
            entity.add(DestroyComponent())
        }
    }

    private fun handleEnemyHittingObstacle(contactType: ContactType.EnemyAndObstacle) {
        if (!contactType.enemy.hasCollidedWithObstacle())
            contactType.enemy.addComponent<CollidedWithObstacle>()
    }

    fun handleGrenadeHittingAnything(contactType: ContactType.GrenadeHittingAnything) {
        //This should be timed using cooldown, not this way
        audioPlayer.playOnChannel(AudioChannels.simultaneous, Assets.newSoundEffects["weapons"]!!["grenade"]!!.random())
        val grenade = contactType.grenade
        val grenadeComponent = grenade.getComponent<GrenadeComponent>()
        val body = grenade.getComponent<BodyComponent>().body!!
        body.linearVelocity.set(Vector2.Zero)

        /*
        Now, add five-ten entities / bodies that fly out in the direction of the molotov
        and also spew fire particles.
         */

        //Add explosion effect entity
        explosionEffectEntity(body.worldCenter)

        //Find all enemies with an area
        val enemiesInRange = engine().getEntitiesFor(allOf(AgentProperties::class).get())
            .filter { it.transform().position.dst(body.worldCenter) < 50f }

        for (enemy in enemiesInRange) {
            //apply distance-related damage

            //apply impulse to enemy body, hopefully sending them away
            val enemyComponent = AshleyMappers.agentProps.get(enemy)
            //Fix this later.
            //enemyComponent.startCooldown(enemyComponent::stunned, 0.5f)
            val enemyBody = enemy.body()
            val distanceVector = enemyBody.worldCenter.cpy().sub(body.worldCenter)
            val direction = distanceVector.cpy().nor()
            val inverseDistance = 1 / distanceVector.len()
            enemy.getComponent<AttackableProperties>()
                .takeDamage((50f..150f).random() * inverseDistance, grenadeComponent.player.entity)
            enemyComponent.lastShotAngle = direction.angleDeg()
            enemyBody.applyLinearImpulse(
                direction.scl(inverseDistance * (500f..1500f).random()),
                enemyBody.worldCenter,
                true
            )
        }
        grenade.addComponent<DestroyComponent>() //This entity will die and disappear now.
    }

    fun handleMolotovHittingAnything(contactType: ContactType.MolotovHittingAnything) {
        audioPlayer.playOnChannel(AudioChannels.simultaneous, Assets.newSoundEffects["weapons"]!!["molotov"]!!.random())
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