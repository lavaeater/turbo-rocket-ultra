package ecs.systems.ai.utility

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.gameplay.TransformComponent
import factories.engine
import factories.world
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.log.debug
import ktx.math.vec2
import physics.agentProps
import physics.getEntity
import physics.isEntity
import physics.transform
import kotlin.reflect.KClass

class CanISeeThisConsideration<ToLookFor : Component>(
    private val componentClass: KClass<ToLookFor>,
    private val stop: Boolean = true
) : Consideration() {
    private val entitiesToLookForFamily = allOf(componentClass, TransformComponent::class).get()
    private val engine by lazy { engine() }
    override fun normalizedScore(entity: Entity): Float {
        val agentProps = entity.agentProps()
        if (stop) {
            agentProps.speed = 0f
        }
        val agentPosition = entity.transform().position
        /*
        Choose a random viewDirection within fov of current viewingdirection! Or some other technique
         */

        val inrangeEntities = engine.getEntitiesFor(entitiesToLookForFamily)
            .filter { it.transform().position.dst(agentPosition) < agentProps.viewDistance }
            .filter {
                canISeeYouFromHere(
                    agentPosition,
                    agentProps.directionVector,
                    it.transform().position,
                    agentProps.fieldOfView
                )
            }
        debug { "LookForAndStore found ${inrangeEntities.size} entities in range and in the field of view" }
        var haveIseenSomething = false
        for (potential in inrangeEntities) {
            val entityPosition = potential.transform().position
            var lowestFraction = 1f
            var closestFixture: Fixture? = null
            val pointOfHit = vec2()
            val hitNormal = vec2()


            world().rayCast(
                agentPosition,
                entityPosition
            ) { fixture, point, normal, fraction ->
                if (fraction < lowestFraction) {
                    lowestFraction = fraction
                    closestFixture = fixture
                    pointOfHit.set(point)
                    hitNormal.set(normal)
                }
                RayCast.CONTINUE
            }

            if (closestFixture != null && closestFixture!!.isEntity() && inrangeEntities.contains(closestFixture!!.getEntity())) {
                debug { "LookForAndStore - entity at $entityPosition can be seen " }
                haveIseenSomething = true
                break
            }
        }
        return if (haveIseenSomething) 1.0f else 0f
    }
}