package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.graphics.renderables.AnimatedCharacterComponent
import ecs.components.graphics.renderables.AnimatedCharacterSprite
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerMoveSystem(
    private var speed: Float = 25f): IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        BodyComponent::class,
        AnimatedCharacterComponent::class).get(), 10) {

    private val pccMapper = mapperFor<PlayerControlComponent>()
    private val bcMapper = mapperFor<BodyComponent>()
    private val anMapper = mapperFor<AnimatedCharacterComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pcc = pccMapper.get(entity)
        val bc = bcMapper.get(entity)
        val csc = anMapper.get(entity)
        executeMove(pcc, bc, csc)
    }

    private var speedFactor = 1f
    private fun executeMove(
        playerControlComponent: PlayerControlComponent,
        bodyComponent: BodyComponent,
        animatedCharacterComponent: AnimatedCharacterComponent
    ) {
        speedFactor = if(playerControlComponent.triggerPulled) 0.2f else 1f

        val vX = playerControlComponent.walkVector.x * speed * speedFactor
        val vY = playerControlComponent.walkVector.y * speed * speedFactor
        bodyComponent.body.setLinearVelocity(vX, vY)

        animatedCharacterComponent.currentAnimState = playerControlComponent.playerAnimState
    }
}
