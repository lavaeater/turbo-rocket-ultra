package ecs.systems.ai.towers

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import data.Players
import eater.ecs.ashley.components.Box2d
import eater.ecs.ashley.components.TransformComponent
import ecs.components.enemy.AttackableProperties
import eater.injection.InjectionContext.Companion.inject
import eater.physics.getComponent
import eater.physics.getEntity
import eater.physics.has
import eater.physics.isEntity
import ecs.components.towers.Shoot
import ecs.components.towers.TargetInRange
import factories.splatterEntity
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.minus
import ktx.math.random
import ktx.math.vec2
import physics.*

class TowerShootSystem: IteratingSystem(allOf(Shoot::class, Box2d::class).get()) {
    private val world: World by lazy { inject() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val shootComponent = entity.getComponent<Shoot>()
        if(!entity.has<TargetInRange>())
            shootComponent.status = Task.Status.FAILED
        else {
            val towerPosition = entity.getComponent<TransformComponent>().position
            val targetInRange = entity.getComponent<TargetInRange>()
            val targetPosition = targetInRange.targetPosition.cpy()
            shootComponent.coolDown -= deltaTime
            if(shootComponent.coolDown < 0f) {
                shootComponent.reset()
/*
Here's the deal. We wanna simulate some vibrations / recoil in the gun that is shooting.
So, we take a position, calculate an angle to the target position, then add some noise to
that angle and THEN we raycast across the map. Fun fun fun

Later we can add the tower needing to rotate to the proper angle (this can be it's own task that runs until it has reached
the desired angle
 */
                targetInRange.aimTarget.set(targetPosition.minus(towerPosition).nor())
                targetInRange.aimTarget.setAngleDeg(targetInRange.aimTarget.angleDeg() + (-10f..10f).random()).setLength(100f)

                var lowestFraction = 1f

                lateinit var closestFixture: Fixture

                val pointOfHit = vec2(0f, 0f)
                val hitNormal = vec2(0f, 0f)

                world.rayCast(towerPosition, targetInRange.aimTarget) { fixture, point, normal, fraction ->

                    if (fraction < lowestFraction && !fixture.isSensor && !fixture.isPlayer()) {
                        lowestFraction = fraction
                        closestFixture = fixture
                        pointOfHit.set(point)
                        hitNormal.set(normal)
                    }
                    RayCast.CONTINUE
                }
                if (lowestFraction < 1f) {
                    if (closestFixture.isEntity() && closestFixture.body.isEnemy()) {
                        val enemyEntity = closestFixture.getEntity()
                        //TODO: FIX PLAYER KILLS WITH EXTRA COMPONENT
                        enemyEntity.getComponent<AttackableProperties>().takeDamage(3f..8f, Players.players.values.random().entity)
                        splatterEntity(closestFixture.body.worldCenter, targetInRange.aimTarget.cpy().nor().angleDeg())
                    }
                    shootComponent.status = Task.Status.SUCCEEDED
                } else {
                    shootComponent.status = Task.Status.FAILED
                }
            } else {
                shootComponent.status = Task.Status.RUNNING
            }
        }


    }
}