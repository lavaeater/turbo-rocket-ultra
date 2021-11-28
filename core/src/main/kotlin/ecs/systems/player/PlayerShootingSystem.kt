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
import factories.thrownProjectile
import features.weapons.Weapon
import features.weapons.WeaponType
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import physics.has
import tru.Assets


class PlayerShootingSystem(private val audioPlayer: AudioPlayer) : IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        WeaponComponent::class,
        TransformComponent::class
    ).get()
) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        To make things a looot easier, we will shoot away bullets and pellets
        These will be represented by some simple sprite, perhaps just a pixel wide
        Yes yes, they could even be drawn by the shapedrawer?
         */
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        controlComponent.coolDown(deltaTime)
        val weapon = entity.getComponent<WeaponComponent>().currentWeapon
        when (weapon.weaponType) {
            WeaponType.Melee -> swingMeleeWeapon(controlComponent, weapon, entity)
            WeaponType.Projectile -> fireProjectileWeapon(controlComponent, weapon, entity)
            WeaponType.ThrownArea -> throwAreaWeapon(controlComponent, weapon, entity)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun throwAreaWeapon(controlComponent: PlayerControlComponent, weapon: Weapon, playerEntity: Entity) {
        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(playerEntity.has<PlayerIsRespawning>() || playerEntity.has<PlayerWaitsForRespawn>())
        ) {
            val transformComponent = playerEntity.getComponent<TransformComponent>()
            playerEntity.getComponent<FiredShotsComponent>().queue.addFirst(transformComponent.position)
            controlComponent.shoot()

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
                thrownProjectile(
                    vec2(
                        transformComponent.position.x + aimVector.x,
                        transformComponent.position.y - 1 + aimVector.y
                    ), aimVector, (15f..25f).random(),
                    controlComponent.player
                )
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun swingMeleeWeapon(controlComponent: PlayerControlComponent, weapon: Weapon, playerEntity: Entity) {
        if (
            controlComponent.firing &&
            !(playerEntity.has<PlayerIsRespawning>() || playerEntity.has<PlayerWaitsForRespawn>())
        ) {
            //1. Check if enemies are within distance (is this faster than the sector first?
            val playerPosition = playerEntity.getComponent<TransformComponent>().position
            val allEnemies =
                engine.getEntitiesFor(allOf(EnemyComponent::class).get())

            val enemiesInRangeAndInHitArc = allEnemies.filter {
                val enemyPosition = it.getComponent<TransformComponent>().position
                enemyPosition.dst(playerPosition) < weapon.spreadOrMeleeRangeOrArea && canISeeYouFromHere(
                    playerPosition,
                    controlComponent.aimVector,
                    enemyPosition,
                    weapon.accuracyOrHitArcForMelee
                )
            }
            enemiesInRangeAndInHitArc.forEach {
                it.getComponent<EnemyComponent>().takeDamage(weapon.damageRange, controlComponent.player)
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
        val returnPolygon = Polygon(floatArray)
        return returnPolygon
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun fireProjectileWeapon(
        controlComponent: PlayerControlComponent,
        weapon: Weapon,
        playerEntity: Entity
    ) {
        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(playerEntity.has<PlayerIsRespawning>() || playerEntity.has<PlayerWaitsForRespawn>())
        ) {
            val transformComponent = playerEntity.getComponent<TransformComponent>()
            playerEntity.getComponent<FiredShotsComponent>().queue.addFirst(transformComponent.position)
            controlComponent.shoot()

            val shotsound = weapon.audio["shot"]!!

            if ((1..5).random() == 1)
                audioPlayer.playSounds(
                    mapOf(
                        shotsound to 0f,
                        Assets.soundEffects["shellcasing"]!! to (0.1f..1f).random()
                    )
                )
            else
                audioPlayer.playSound(shotsound)


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
        }
    }
}

