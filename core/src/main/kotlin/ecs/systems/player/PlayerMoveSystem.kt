package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.components.Box2d
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.getComponent

class PlayerMoveSystem(): IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        Box2d::class,
        AnimatedCharacterComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pcc = entity.getComponent<PlayerControlComponent>()
        if(pcc.cooldownPropertyCheckIfDone(pcc::stunned, deltaTime)) {
            val bc = Box2d.get(entity)
            val csc = entity.getComponent<AnimatedCharacterComponent>()
            executeMove(pcc, bc, csc)
        }
    }

    private fun executeMove(
        playerControlComponent: PlayerControlComponent,
        bodyComponent: Box2d,
        animatedCharacterComponent: AnimatedCharacterComponent
    ) {

        val vX = playerControlComponent.walkVector.x * playerControlComponent.actualSpeed
        val vY = playerControlComponent.walkVector.y * playerControlComponent.actualSpeed
        bodyComponent.body!!.setLinearVelocity(vX, vY)

        animatedCharacterComponent.currentAnimState = playerControlComponent.playerAnimState
    }
}
