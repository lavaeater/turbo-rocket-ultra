package ecs.systems.player

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.*
import factories.bullet
import factories.throwMolotov
import factories.throwGrenade
import features.weapons.Weapon
import features.weapons.WeaponType
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.math.random
import ktx.math.vec2
import physics.AshleyMappers
import physics.playerControlComponent
import physics.weapon
import physics.weaponEntity
import tru.Assets


class PlayerShootingSystem(private val audioPlayer: AudioPlayer) : IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        WeaponEntityComponent::class,
        TransformComponent::class
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        To make things a looot easier, we will shoot away bullets and pellets
        These will be represented by some simple sprite, perhaps just a pixel wide
        Yes yes, they could even be drawn by the shapedrawer?
         */
        val controlComponent = entity.playerControlComponent()
        controlComponent.coolDown(deltaTime)
        val weapon = entity.weaponEntity().weapon().currentWeapon
        when (weapon.weaponType) {
            WeaponType.Melee -> swingMeleeWeapon(controlComponent, weapon, entity)
            WeaponType.Projectile -> fireProjectileWeapon(controlComponent, weapon, entity)
            WeaponType.ThrownWeapon -> trowWeapon(controlComponent, weapon, entity)
        }
    }

    private fun trowWeapon(controlComponent: PlayerControlComponent, weapon: Weapon, playerEntity: Entity) {
        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(AshleyMappers.respawn.has(playerEntity) || AshleyMappers.waitsForRespawn.has(playerEntity))
        ) {
            playSound(controlComponent.player.playerId)
            val transformComponent = AshleyMappers.transform.get(playerEntity)
            controlComponent.shoot()

            weapon.ammoRemaining--
            val aimVector = controlComponent.aimVector.cpy()
            val angle = aimVector.angleDeg()
            val angleVariation = (-weapon.accuracyOrHitArcForMelee..weapon.accuracyOrHitArcForMelee).random()
            aimVector.setAngleDeg(angle + angleVariation)

            for (projectile in 0 until weapon.numberOfProjectiles) {
                // If only one projectile, it is simple, else, there is trouble
                /*
                For multi-projectile guns with spread (shotguns)
                We will calculate a starting aimvector based on spread / 2
                and then add increments of angle based on spread / numberOf projectiles
                The accuracy will be done by creating local aimVector varied by the
                accuracy of the gun, ONCE
                 */
                if (weapon.numberOfProjectiles != 1) {
                    if (projectile == 0) {
                        aimVector.setAngleDeg(aimVector.angleDeg() - weapon.spreadOrMeleeRangeOrArea / 2)
                    } else {
                        aimVector.setAngleDeg(aimVector.angleDeg() + weapon.spreadOrMeleeRangeOrArea / weapon.numberOfProjectiles)
                    }
                }
                /**
                 * Create a bullet entity at aimVector that travels very fast
                 */
                when (weapon.name) {
                    "Molotov Cocktail" -> throwMolotov(
                        vec2(
                            transformComponent.position.x + aimVector.x,
                            transformComponent.position.y - 1 + aimVector.y
                        ), aimVector, (15f..25f).random(),
                        controlComponent.player
                    )
                    "Grenade" -> throwGrenade(
                        vec2(
                            transformComponent.position.x + aimVector.x,
                            transformComponent.position.y - 1 + aimVector.y
                        ), aimVector, (15f..25f).random(),
                        controlComponent.player
                    )
                }

            }
        } else if (weapon.ammoRemaining <= 0) {
            audioPlayer.playOnChannel(controlComponent.player.playerId, "players", "out-of-ammo")
        }
    }

    private fun playSound(playerId: String) {
        if ((1..5).random() == 1)
            audioPlayer.playOnChannel(playerId, "players", "one-liners")
    }

    private fun swingMeleeWeapon(controlComponent: PlayerControlComponent, weapon: Weapon, playerEntity: Entity) {
        if (
            controlComponent.firing &&
            !(AshleyMappers.respawn.has(playerEntity) || AshleyMappers.waitsForRespawn.has(playerEntity))
        ) {
            playSound(controlComponent.player.playerId)
            //1. Check if enemies are within distance (is this faster than the sector first?
            val playerPosition = AshleyMappers.transform.get(playerEntity).position
            val allEnemies =
                engine.getEntitiesFor(allOf(EnemyComponent::class).get())

            val enemiesInRangeAndInHitArc = allEnemies.filter {
                val enemyPosition = AshleyMappers.transform.get(it).position
                enemyPosition.dst(playerPosition) < weapon.spreadOrMeleeRangeOrArea && canISeeYouFromHere(
                    playerPosition,
                    controlComponent.aimVector,
                    enemyPosition,
                    weapon.accuracyOrHitArcForMelee
                )
            }
            enemiesInRangeAndInHitArc.forEach {
                AshleyMappers.enemy.get(it).takeDamage(weapon.damageRange, controlComponent.player)
            }
        }
    }

    fun createScanPolygon(
        start: Vector2,
        viewDirection: Vector2,
        viewDistance: Float,
        fov: Float,
        step: Float
    ): Polygon {
        val numberOfSteps = (fov / step).toInt()

        val direction = viewDirection.cpy().setAngleDeg(viewDirection.angleDeg() - (fov / 2) - step)
        val points = mutableListOf<Vector2>()
        points.add(start)
        for (i in 0..numberOfSteps) {
            direction.setAngleDeg(direction.angleDeg() + step)
            val pointToAdd = vec2(start.x, start.y)
                .add(direction)
                .sub(start)
                .scl(viewDistance)
                .add(start)
                .add(direction)
            points.add(pointToAdd)
        }
        val floatArray = points.map { listOf(it.x, it.y) }.flatten().toFloatArray()
        return Polygon(floatArray)
    }

    private fun fireProjectileWeapon(
        controlComponent: PlayerControlComponent,
        weapon: Weapon,
        playerEntity: Entity
    ) {
        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(AshleyMappers.respawn.has(playerEntity) || AshleyMappers.waitsForRespawn.has(playerEntity))
        ) {
            playSound(controlComponent.player.playerId)
            val transformComponent = AshleyMappers.transform.get(playerEntity)
            AshleyMappers.firedShots.get(playerEntity).queue.addFirst(
                Pair(
                    transformComponent.position,
                    weapon.soundRadius
                )
            )
            controlComponent.shoot()

            val shotsound = weapon.audio["shot"]!!
            shotsound.play()

            //TODO: Fix combined audio of shots fired and shell casings dropping on floor
//            if ((1..5).random() == 1)
//                audioPlayer.playSounds(
//                    mapOf(
//                        shotsound to 0f,
//                        Assets.soundEffects["shellcasing"]!! to (0.1f..1f).random()
//                    )
//                )
//            else
//                audioPlayer.playSound(shotsound)


            weapon.ammoRemaining--
            val aimVector = controlComponent.aimVector.cpy()
            val angle = aimVector.angleDeg()
            val angleVariation = (-weapon.accuracyOrHitArcForMelee..weapon.accuracyOrHitArcForMelee).random()
            aimVector.setAngleDeg(angle + angleVariation)
            for (projectile in 0..weapon.numberOfProjectiles) {
                // If only one projectile, it is simple, else, there is trouble
                /*
                For multi-projectile guns with spread (shotguns)
                We will calculate a starting aimvector based on spread / 2
                and then add increments of angle based on spread / numberOf projectiles
                The accuracy will be done by creating local aimVector varied by the
                accuracy of the gun, ONCE
                 */
                if (weapon.numberOfProjectiles != 1) {
                    if (projectile == 0) {
                        aimVector.setAngleDeg(aimVector.angleDeg() - weapon.spreadOrMeleeRangeOrArea / 2)
                    } else {
                        aimVector.setAngleDeg(aimVector.angleDeg() + weapon.spreadOrMeleeRangeOrArea / weapon.numberOfProjectiles)
                    }
                }
                /**
                 * Create a bullet entity at aimVector that travels very fast
                 */
                bullet(
                    vec2(
                        transformComponent.position.x + aimVector.x,
                        transformComponent.position.y + aimVector.y
                    ),
                    aimVector,
                    (75f..175f).random(),
                    weapon.damageRange.random(),
                    controlComponent.player
                )
            }
        } else if (weapon.ammoRemaining <= 0 && controlComponent.canPlay(Sfx.outofAmmo)) {
            audioPlayer.playOnChannel(controlComponent.player.playerId, "players", Sfx.outofAmmo)
            controlComponent.hasPlayed(Sfx.outofAmmo)
        }
    }
}

object Sfx {
    const val outofAmmo = "out-of-ammo"
}

