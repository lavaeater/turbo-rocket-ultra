package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import ecs.components.EnemyComponent
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
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

class PlayerShootingSystem : IteratingSystem(
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
            controlComponent.shoot()
            val end = controlComponent.aimVector.cpy().nor().scl(50f)

            controlComponent.latestHitPoint.set(end)

            //create raycast to find some targets
            val transform = transformMapper[entity]

            val start = transform.position

            var lowestFraction = 1f

            lateinit var closestFixture: Fixture

            val pointOfHit = vec2(0f, 0f)
            val hitNormal = vec2(0f, 0f)

            world.rayCast(start, end) { fixture, point, normal, fraction ->

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