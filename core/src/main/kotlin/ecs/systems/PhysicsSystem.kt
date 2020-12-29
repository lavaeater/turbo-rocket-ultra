package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PhysicsSystem (private val world: World) :
    IteratingSystem(allOf(
        TransformComponent::class,
        BodyComponent::class).get(), 10) {

    private val MAX_STEP_TIME = 1 / 300f
    private var accumulator = 0f
    private val bodyMapper = mapperFor<BodyComponent>()
    private val transMapper = mapperFor<TransformComponent>()

    override fun update(deltaTime: Float) {

        val frameTime = deltaTime.coerceAtMost(0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(deltaTime, 6, 2)
            accumulator -= MAX_STEP_TIME
        }
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = bodyMapper.get(entity)!!
        val transformComponent = transMapper.get(entity)!!
        transformComponent.position.set(bodyComponent.body.position)
        transformComponent.rotation = bodyComponent.body.angle
    }
}