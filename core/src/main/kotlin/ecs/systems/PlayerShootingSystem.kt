package ecs.systems

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ecs.components.EnemyComponent
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import factories.splatterParticles
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import physics.getEntity
import physics.isEnemy
import physics.isEntity


/**
 * We need a cool-down system, which determines the rate of fire.
 *
 * This means you can always shoot if the weapon is cool.
 */

class PlayerShootingSystem(private val audioPlayer: AudioPlayer) : IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        TransformComponent::class
    ).get()
) {
    private val controlMapper = mapperFor<PlayerControlComponent>()
    private val transformMapper = mapperFor<TransformComponent>()
    private val world: World by lazy { inject() }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {

        val controlComponent = controlMapper[entity]
        controlComponent.coolDown(deltaTime)

        if (controlComponent.firing) {
            //create raycast to find some targets
            val transform = transformMapper[entity]
            controlComponent.shoot()
            if((1..5).random() == 1)
                audioPlayer.playSounds(mapOf("gunshot" to 0f, "shellcasing" to (0.1f..1f).random()))
            else
                audioPlayer.playSound("gunshot")

            /*
            This point should be:
            The players position PLUS the aimVector (if aiming left, it is negative, and so on. This is the

            https://www.debugcn.com/en/article/63417562.html
             */

            controlComponent.latestHitPoint
                .set(transform.position)
                .add(controlComponent.aimVector)
                .sub(transform.position)
                .scl(50f)
                .add(transform.position)
                .add(controlComponent.aimVector)

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
                    enemyEntity.getComponent<EnemyComponent>().takeDamage(10..25)
                    splatterParticles(closestFixture.body, controlComponent.aimVector.cpy())
                }
            }
        }
    }
}