package ai.behaviorTree.tasks.leaf.boss

import ai.behaviorTree.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.Box2d
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.transform

/**
 * Boss grabs the nearest player in melee range, stuns them for [stunDuration] seconds,
 * throws them in the direction away from the boss, and deals [damage].
 */
class GrabAndThrowPlayer(
    val grabRange: Float = 2f,
    val stunDuration: Float = 1.5f,
    val throwForce: Float = 25f,
    val damage: Float = 30f
) : EntityTask() {
    private val playerFamily = allOf(
        PlayerComponent::class,
        TransformComponent::class,
        AttackableProperties::class,
        PlayerControlComponent::class,
        Box2d::class
    ).get()

    override fun execute(): Status {
        val bossPos = entity.transform().position
        val player = engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(bossPos) <= grabRange }
            .minByOrNull { it.transform().position.dst(bossPos) }
            ?: return Status.FAILED

        val pcc = PlayerControlComponent.get(player)
        val body = Box2d.get(player).body

        // Stun: player loses control for stunDuration seconds
        pcc.startCooldown(pcc::stunned, stunDuration)

        // Throw: impulse away from the boss
        val throwDir = player.transform().position.cpy().sub(bossPos).nor()
        body.applyLinearImpulse(throwDir.scl(throwForce * body.mass), body.worldCenter, true)

        // Damage
        AttackableProperties.get(player).takeDamage(damage, entity)

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> =
        GrabAndThrowPlayer(grabRange, stunDuration, throwForce, damage)

    override fun toString() = "Grab and Throw Player"
}
