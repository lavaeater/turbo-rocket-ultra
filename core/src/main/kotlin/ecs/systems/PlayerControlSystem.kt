package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import ecs.components.BodyComponent
import ecs.components.CharacterSpriteComponent
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import factories.shot
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2
import tru.AnimState

class PlayerControlSystem(
    private val torque: Float = 1f,
    private val thrust: Float = 25f): IteratingSystem(
    allOf(
        PlayerControlComponent::class,
        BodyComponent::class,
        CharacterSpriteComponent::class).get(), 10) {

    private val pccMapper = mapperFor<PlayerControlComponent>()
    private val bcMapper = mapperFor<BodyComponent>()
    private val anMapper = mapperFor<CharacterSpriteComponent>()
    private val tMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pcc = pccMapper.get(entity)
        val bc = bcMapper.get(entity)
        val csc = anMapper.get(entity)
        val tc = tMapper.get(entity)
        handleInput(pcc, bc, csc, tc)
    }

    private fun handleInput(
        playerControlComponent: PlayerControlComponent,
        bodyComponent: BodyComponent,
        characterSpriteComponent: CharacterSpriteComponent,
        transformComponent: TransformComponent) {
        if (playerControlComponent.turning != 0f) {
            bodyComponent.body.applyTorque(torque * playerControlComponent.turning, true)
        }

        val forceVector = vec2(MathUtils.cos(bodyComponent.body.angle), MathUtils.sin(bodyComponent.body.angle)).rotate90(1)

        if (playerControlComponent.walking != 0f) {
            bodyComponent.body.applyForceToCenter(forceVector.scl(thrust), true)
            characterSpriteComponent.currentAnimState = AnimState.Walk
        } else {
            characterSpriteComponent.currentAnimState = AnimState.Idle
        }

        //Throw in polling of mouse coordinates here?
        playerControlComponent.setAimVector(Gdx.input.x, Gdx.input.y, transformComponent.position)
    }
}