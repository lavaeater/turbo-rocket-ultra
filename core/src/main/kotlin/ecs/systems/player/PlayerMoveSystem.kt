package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.graphics.CharacterSpriteComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerMoveSystem(
    private var speed: Float = 25f): IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        BodyComponent::class,
        CharacterSpriteComponent::class).get(), 10) {

    private val pccMapper = mapperFor<PlayerControlComponent>()
    private val bcMapper = mapperFor<BodyComponent>()
    private val anMapper = mapperFor<CharacterSpriteComponent>()

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
        characterSpriteComponent: CharacterSpriteComponent
    ) {
        speedFactor = if(playerControlComponent.triggerPulled) 0.2f else 1f
        bodyComponent.body.setLinearVelocity(playerControlComponent.walkVector.x * speed * speedFactor, playerControlComponent.walkVector.y * speed * speedFactor)

        characterSpriteComponent.currentAnimState = playerControlComponent.playerAnimState
    }
}