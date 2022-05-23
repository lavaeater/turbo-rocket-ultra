package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.BodyComponent
import ecs.components.enemy.AgentProperties
import ecs.components.enemy.AttackableProperties
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.*
import tru.Assets

class EnemyMovementSystem(private val flocking: Boolean) : IteratingSystem(
    allOf(
        AgentProperties::class,
        BodyComponent::class,
        TransformComponent::class
    ).get()
) {
    private val separationRange = 10f
    private val enemyFamily = allOf(AgentProperties::class).get()
    private val allEnemies get() = engine.getEntitiesFor(enemyFamily)
    private val obstacleFamily = allOf(ObstacleComponent::class).get()
    private val allObstacles get() = engine.getEntitiesFor(obstacleFamily)

    private val alignment = vec2()
    private val cohesion = vec2()
    private val separation = vec2()
    private val obstacleAvoidance = vec2()
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity.agentProps()
        val attackable = entity.getComponent<AttackableProperties>()
        if (enemyComponent.cooldownPropertyCheckIfDone(attackable::stunned, deltaTime)) {
            val bodyComponent = AshleyMappers.body.get(entity)
            if (flocking && enemyComponent.flock && entity.sprite().isVisible) {
                fixFlocking(bodyComponent.body!!)
            }
            avoidObstacles(bodyComponent.body!!.position)
            moveEnemy(enemyComponent, bodyComponent)
        }
    }

    private fun fixFlocking(body: Body) {
        computeFlocking(body.worldCenter)
    }

    private fun computeFlocking(enemyPosition: Vector2) {
        val sep = vec2()
        val coh = vec2()
        val ali = vec2()
        var count = 0
        for (enemy in allEnemies) {
            val position = enemy.transform().position
            if (position.dst(enemyPosition) < separationRange) {
                sep.x += position.x - enemyPosition.x
                sep.y += position.y - enemyPosition.y
                coh.x += position.x
                coh.y += position.y
                val velocity = AshleyMappers.body.get(enemy).body!!.linearVelocity
                ali.x += velocity.x
                ali.y += velocity.y
                count++
            }
        }
        if (count > 0) {
            sep.x /= count
            sep.y /= count
            sep.x *= -1
            sep.y *= -1
            separation.set(sep.nor())
            coh.x /= count
            coh.y /= count
            coh.set(coh.sub(enemyPosition))
            cohesion.set(coh.nor())
            ali.x /= count
            ali.y /= count
            alignment.set(ali.nor())
        }
    }

    private fun avoidObstacles(enemyPosition: Vector2) {
        val avoid = vec2()
        var count = 0
        for(obstacle in allObstacles) {
            val position = obstacle.transform().position
            val distance = position.dst(enemyPosition)
            if(distance < 5f) {
                //The closer we get, the more we avoid the obstacle!
                avoid.x += position.x - enemyPosition.x
                avoid.y += position.y - enemyPosition.y
                count++
            }
        }
        if(count > 0) {
            avoid.x /= count
            avoid.y /= count
            avoid.x *= -1
            avoid.y *= -1
        }
        obstacleAvoidance.set(avoid).nor()
    }

    private val actualDirectionVector = vec2()
    private val circleColor = Color(1f, 0f, 0f, 0.1f)

    private fun moveEnemy(enemyComponent: AgentProperties, bodyComponent: BodyComponent) {
        //.
        actualDirectionVector.set(enemyComponent.directionVector)
        if(flocking)
            actualDirectionVector.add(cohesion.scl(.3f)).add(separation.scl(0.7f))
            .add(alignment.scl(.5f))
        actualDirectionVector.add(obstacleAvoidance.scl(.8f)).nor()
        bodyComponent.body!!.linearVelocity = actualDirectionVector.scl(enemyComponent.speed)
    }

    private fun handleObstacles(enemyComponent: AgentProperties, bodyComponent: BodyComponent) {

    }
}

