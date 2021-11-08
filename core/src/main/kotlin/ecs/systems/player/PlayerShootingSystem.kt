package ecs.systems.player

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.BulletComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.*
import factories.bullet
import factories.splatterEntity
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.random
import ktx.math.vec2
import physics.*


class NewPlayerShootingSystem(private val audioPlayer: AudioPlayer) : IteratingSystem(
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
        val weapon = entity.getComponent<WeaponComponent>().currentGun
        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(entity.hasComponent<PlayerIsRespawning>() || entity.hasComponent<PlayerWaitsForRespawn>())
        ) {
            val transformComponent = entity.getComponent<TransformComponent>()
            entity.getComponent<FiredShotsComponent>().queue.addFirst(transformComponent.position)
            controlComponent.shoot()

            if ((1..5).random() == 1)
                audioPlayer.playSounds(mapOf("gunshot" to 0f, "shellcasing" to (0.1f..1f).random()))
            else
                audioPlayer.playSound("gunshot")


            weapon.ammoRemaining--
            val aimVector = controlComponent.aimVector.cpy()
            val angle = aimVector.angleDeg()
            val angleVariation = (-weapon.accuracy..weapon.accuracy).random()
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
                        aimVector.setAngleDeg(aimVector.angleDeg() - weapon.maxSpread / 2)
                    } else {
                        aimVector.setAngleDeg(aimVector.angleDeg() + weapon.maxSpread / weapon.numberOfProjectiles)
                    }
                }
                /**
                 * Create a bullet entity at aimVector that travels very fast
                 */
                bullet(vec2(transformComponent.position.x + aimVector.x, transformComponent.position.y + aimVector.y), aimVector, (75f..150f).random(), weapon.damageRange.random())
            }
        }
    }
}

class BulletSpeedSystem: IteratingSystem(allOf(BulletComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent<BodyComponent>()
        val linearVelocity = body.body.linearVelocity
        //body.body.linearVelocity = linearVelocity.setLength(100f)
    }

}

class PlayerShootingSystem(private val audioPlayer: AudioPlayer) : IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        WeaponComponent::class,
        TransformComponent::class
    ).get()
) {
    private val controlMapper = mapperFor<PlayerControlComponent>()
    private val transformMapper = mapperFor<TransformComponent>()
    private val shotsFiredMapper = mapperFor<FiredShotsComponent>()
    private val weaponsMapper = mapperFor<WeaponComponent>()
    private val world: World by lazy { inject() }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {

        val controlComponent = controlMapper[entity]
        controlComponent.coolDown(deltaTime)
        val weapon = weaponsMapper.get(entity).currentGun

        if (
            controlComponent.firing &&
            weapon.ammoRemaining > 0 &&
            !(entity.hasComponent<PlayerIsRespawning>() || entity.hasComponent<PlayerWaitsForRespawn>())
        ) {
            val transform = transformMapper[entity]
            shotsFiredMapper[entity].queue.addFirst(transform.position)
            /*

            Send a message to the "noticing system" for every shot
             */

            //create raycast to find some targets
            controlComponent.shoot()
            if ((1..5).random() == 1)
                audioPlayer.playSounds(mapOf("gunshot" to 0f, "shellcasing" to (0.1f..1f).random()))
            else
                audioPlayer.playSound("gunshot")

            /*
            This point should be:
            The players position PLUS the aimVector (if aiming left, it is negative, and so on. This is the

            https://www.debugcn.com/en/article/63417562.html

            Adding different weapons to fuck shit up!
             */

            //DO a ray-cast for every projectile in the shot
            //But first, reduce ammo by one
            weapon.ammoRemaining--
            val aimVector = controlComponent.aimVector.cpy()
            val angle = aimVector.angleDeg()
            val angleVariation = (-weapon.accuracy..weapon.accuracy).random()
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
                        aimVector.setAngleDeg(aimVector.angleDeg() - weapon.maxSpread / 2)
                    } else {
                        aimVector.setAngleDeg(aimVector.angleDeg() + weapon.maxSpread / weapon.numberOfProjectiles)
                    }
                }

                controlComponent.latestHitPoint
                    .set(transform.position)
                    .add(aimVector)
                    .sub(transform.position)
                    .scl(50f)
                    .add(transform.position)
                    .add(aimVector)

                val start = transform.position

                var lowestFraction = 1f

                lateinit var closestFixture: Fixture

                val pointOfHit = vec2(0f, 0f)
                val hitNormal = vec2(0f, 0f)

                world.rayCast(start, controlComponent.latestHitPoint) { fixture, point, normal, fraction ->

                    if (fraction < lowestFraction && !fixture.isSensor) {
                        lowestFraction = fraction
                        closestFixture = fixture
                        pointOfHit.set(point)
                        hitNormal.set(normal)
                    }
                    RayCast.CONTINUE
                }
                if (lowestFraction < 1f) {
                    controlComponent.latestHitPoint.set(pointOfHit)
                    //we have a hit!
                    if (closestFixture.isEntity() && closestFixture.body.isEnemy()) {
                        val enemyEntity = closestFixture.getEntity()

                        enemyEntity.getComponent<EnemyComponent>().takeDamage(weapon.damageRange)
                        if (enemyEntity.getComponent<EnemyComponent>().health < 0) {
                            entity.getComponent<PlayerComponent>().player.kills++
                        }
                        splatterEntity(
                            closestFixture.body.worldCenter,
                            controlComponent.aimVector.cpy().nor().angleDeg()
                        )
                    }
                }
            }
        }
    }
}