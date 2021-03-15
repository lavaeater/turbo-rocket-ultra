package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PhysicsSystem(private val world: World) :
    IteratingSystem(
        allOf(
            TransformComponent::class,
            BodyComponent::class
        ).get(), 1) {

    private val MAX_STEP_TIME = 1 / 60f
    private var accumulator = 0f
    private var interpolator = 1f
    private val bodyMapper = mapperFor<BodyComponent>()
    private val transMapper = mapperFor<TransformComponent>()

    override fun update(deltaTime: Float) {
        val frameTime = deltaTime.coerceAtMost(0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(deltaTime, 6, 2)
            accumulator -= MAX_STEP_TIME
        }
        interpolator = accumulator / MAX_STEP_TIME
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = bodyMapper.get(entity)!!
        val bodyPosition = bodyComponent.body.position
        val bodyRotation = bodyComponent.body.angle
        val transformComponent = transMapper.get(entity)!!

        transformComponent.position.x = bodyPosition.x * interpolator + transformComponent.position.x * (1f - interpolator)
        transformComponent.position.y = bodyPosition.y * interpolator + transformComponent.position.y * (1f - interpolator)
        transformComponent.rotation = bodyRotation * interpolator + transformComponent.rotation * (1f - interpolator)
    }
}