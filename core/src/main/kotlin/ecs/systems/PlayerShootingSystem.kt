package ecs.systems

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ecs.components.EnemyComponent
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import factories.enemy
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
            audioPlayer.playSounds(mapOf("gunshot" to 0f, "shellcasing" to (0.1f..0.5f).random()))

            controlComponent.latestHitPoint.set(controlComponent.aimVector).sub(transform.position).scl(20f).add(transform.position)


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
                }
            }
        }
    }
}