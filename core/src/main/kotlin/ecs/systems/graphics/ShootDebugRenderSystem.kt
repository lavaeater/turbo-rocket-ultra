package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.PlayerControlComponent
import ecs.components.gameplay.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import tru.Assets


class ShootDebugRenderSystem(private val debug: Boolean = false, private val renderRedDot: Boolean = true) :
    IteratingSystem(
        allOf(
            TransformComponent::class,
            PlayerControlComponent::class
        ).get()
    ) {

    private val controlMapper = mapperFor<PlayerControlComponent>()
    private val transformMapper = mapperFor<TransformComponent>()

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val aimVector = vec2(0f, 0f)

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = controlMapper[entity]
        val transform = transformMapper[entity]
        aimVector.set(controlComponent.aimVector)

//        if (controlComponent.drawShot) {
//            batch.begin()
//                shapeDrawer.line(transform.position, controlComponent.latestHitPoint, Color.GREEN, 0.1f)
//            batch.end()
//        }
        if (renderRedDot) {
                shapeDrawer.line(transform.position, aimVector.setLength(50f).add(transform.position), Color.RED, .1f)
        }

        if (debug) {
            shapeDrawer.line(transform.position, aimVector.add(transform.position), Color.BLUE, 0.2f)
            shapeDrawer.line(transform.position, controlComponent.mousePosition, Color.RED, 0.05f)
        }
    }

}