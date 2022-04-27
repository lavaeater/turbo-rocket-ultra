package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.getComponent

class PlayerMoveSystem(private var speed: Float): IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        BodyComponent::class,
        AnimatedCharacterComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pcc = entity.getComponent<PlayerControlComponent>()
        if(pcc.cooldownPropertyCheckIfDone(pcc::stunned, deltaTime)) {
            val bc = entity.getComponent<BodyComponent>()
            val csc = entity.getComponent<AnimatedCharacterComponent>()
            executeMove(pcc, bc, csc)
        }
    }

    private var speedFactor = 1f
    private fun executeMove(
        playerControlComponent: PlayerControlComponent,
        bodyComponent: BodyComponent,
        animatedCharacterComponent: AnimatedCharacterComponent
    ) {
        speedFactor = if(playerControlComponent.waitsForRespawn) 0f else if(playerControlComponent.triggerPulled) 0.2f else 1f

        val vX = playerControlComponent.walkVector.x * speed * speedFactor
        val vY = playerControlComponent.walkVector.y * speed * speedFactor
        bodyComponent.body!!.setLinearVelocity(vX, vY)

        animatedCharacterComponent.currentAnimState = playerControlComponent.playerAnimState
    }
}
