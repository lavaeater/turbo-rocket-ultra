package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.*
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.CharacterSpriteComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerMoveSystem(
    private val speed: Float = 25f): IteratingSystem(
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

    private fun executeMove(
        playerControlComponent: PlayerControlComponent,
        bodyComponent: BodyComponent,
        characterSpriteComponent: CharacterSpriteComponent
    ) {

        bodyComponent.body.setLinearVelocity(playerControlComponent.walkVector.x * speed, playerControlComponent.walkVector.y * speed)

        characterSpriteComponent.currentAnimState = playerControlComponent.playerAnimState
    }
}