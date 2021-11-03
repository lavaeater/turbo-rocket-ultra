package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.*
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import physics.AshleyMappers

class PhysicsSystem(private val world: World, private val timeStep : Float = 1/60f) :
    IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get()) {

    private val velIters = 2
    private val posIters = 2
    private val tMapper = AshleyMappers.transformMapper
    private val bMapper = AshleyMappers.bodyMapper

    var accumulator = 0f

    override fun update(deltaTime: Float) {
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while(accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            accumulator -= ourTime
        }
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //supposed to be empty - is never called because we don't call super.update in update override
//        for(entity in entities) {
            val bodyComponent = bMapper.get(entity)!!
            val bodyPosition = bodyComponent.body.position
            val bodyRotation = bodyComponent.body.angle
            val transformComponent = tMapper.get(entity)!!
            transformComponent.position.set(bodyPosition)
            transformComponent.rotation = bodyRotation

//                if(entity.has(pMapper)) {
//                    val pC = pMapper.get(entity)
//                    val bloodEntity = engine.createEntity().apply {
//                        add(engine.createComponent(TransformComponent::class.java).apply {
//                            position.set(transformComponent.position)
//                            rotation = transformComponent.rotation
//                        })
//                        add(engine.createComponent(SplatterComponent::class.java).apply {
//                            radius = .1f
//                            color = pC.color
//                            life = pC.life / 2f
//                        })
//                        add(engine.createComponent(RenderableComponent::class.java))
//                    }
//                    engine.addEntity(bloodEntity)
//                }
//        }
    }

}