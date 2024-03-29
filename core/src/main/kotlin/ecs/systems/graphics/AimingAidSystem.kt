package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import eater.ecs.ashley.components.TransformComponent
import eater.injection.InjectionContext.Companion.inject
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.vec2
import eater.physics.getComponent
import tru.Assets


class AimingAidSystem(private val debug: Boolean, private val renderRedDot: Boolean) :
    IteratingSystem(
        allOf(
            TransformComponent::class,
            PlayerControlComponent::class
        ).get()) {

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val aimVector = vec2(0f, 0f)

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        val transform = entity.getComponent<TransformComponent>()
        aimVector.set(controlComponent.aimVector)
        if (renderRedDot) {
            shapeDrawer.line(
                vec2(transform.position.x + aimVector.x, transform.position.y + aimVector.y),
                aimVector.setLength(50f).add(transform.position),
                Color(1f, 0f, 0f, .1f),
                .1f
            )
        }
    }
}