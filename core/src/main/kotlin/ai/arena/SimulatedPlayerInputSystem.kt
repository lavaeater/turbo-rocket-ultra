package ai.arena

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.AgentProperties
import components.TransformComponent
import components.player.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.transform
import physics.agentProps
import kotlin.random.Random

/**
 * Drives a simulated player entity. The "bot" strategy:
 * - Moves toward the enemy when far away, backs off when very close.
 * - Introduces slight jitter so it isn't perfectly predictable.
 * - Does not shoot (combat is melee-driven in the arena).
 *
 * Seeded by [seed] for reproducible runs.
 */
class SimulatedPlayerInputSystem(
    private val enemyProvider: () -> Entity?,
    private val seed: Long = 42L
) : IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, AgentProperties::class).get()) {

    private val rng = Random(seed)
    private var jitterTimer = 0f
    private var jitterAngle = 0f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val myPos = entity.transform().position
        val enemy = enemyProvider() ?: run {
            entity.agentProps().speed = 0f
            return
        }
        val enemyPos = enemy.transform().position
        val dist = myPos.dst(enemyPos)

        jitterTimer -= deltaTime
        if (jitterTimer <= 0f) {
            jitterAngle = rng.nextFloat() * 60f - 30f
            jitterTimer = 0.3f + rng.nextFloat() * 0.4f
        }

        val dir = entity.agentProps().directionVector
        dir.set(enemyPos).sub(myPos).nor()
        if (dist < 4f) dir.scl(-1f) // back off when too close
        dir.rotateDeg(jitterAngle)

        entity.agentProps().speed = entity.agentProps().baseProperties.speed
    }
}
