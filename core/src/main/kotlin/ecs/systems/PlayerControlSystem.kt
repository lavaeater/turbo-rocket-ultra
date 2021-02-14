package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ecs.components.BodyComponent
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import factories.shot
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2

class PlayerControlSystem(
    private val rof: Float = 0.1f,
    private val torque: Float = 5f,
    private val thrust: Float = 50f): IteratingSystem(allOf(PlayerControlComponent::class, BodyComponent::class).get()) {
    private var lastShot = 0f
    private val pccMapper = mapperFor<PlayerControlComponent>()
    private val bcMapper = mapperFor<BodyComponent>()
    private val tcMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pcc = pccMapper.get(entity)
        val bc = bcMapper.get(entity)
        val tc = tcMapper.get(entity)
        handleShooting(pcc, tc, deltaTime)
        handleInput(pcc, bc)
    }

    private fun handleShooting(playerControlComponent: PlayerControlComponent, transformComponent: TransformComponent, delta: Float) {
        if (playerControlComponent.firing) {
            lastShot += delta
            if (lastShot > rof) {
                lastShot = 0f
                shot(transformComponent.position.cpy().add(playerControlComponent.aimVector.cpy().scl(3f)),
                    playerControlComponent.aimVector.cpy())
            }
        }
    }

    private fun handleInput(playerControlComponent: PlayerControlComponent, bodyComponent: BodyComponent) {
        if (playerControlComponent.turning != 0f) {
            bodyComponent.body.applyTorque(torque * playerControlComponent.turning, true)
        }

        val forceVector = vec2(MathUtils.cos(bodyComponent.body.angle), MathUtils.sin(bodyComponent.body.angle)).rotate90(1)

        if (playerControlComponent.walking != 0f)
            bodyComponent.body.applyForceToCenter(forceVector.scl(thrust), true)
    }
}