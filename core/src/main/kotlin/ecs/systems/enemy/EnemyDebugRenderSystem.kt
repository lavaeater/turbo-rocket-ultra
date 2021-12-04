package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.ai.*
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import physics.getComponent
import physics.has
import tru.Assets

//TODO: Combine this with the new debugrendersystem to render sprites instead of stupid balls
class EnemyDebugRenderSystem(
    private val renderStates: Boolean,
    private val renderScans: Boolean
) : IteratingSystem(
    allOf(
        EnemyComponent::class,
        TransformComponent::class
    ).get()
) {

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (renderStates) {
            var color = Color.GREEN
            when {
                entity.has<Investigate>() -> color = Color.PURPLE
                entity.has<Amble>() -> color = Color.GREEN
                entity.has<SeekPlayer>() -> color = Color.CYAN
                entity.has<ChasePlayer>() -> color = Color.YELLOW
                entity.has<AttackPlayer>() -> color = Color.RED
            }
            shapeDrawer.filledCircle(entity.getComponent<TransformComponent>().position, 1.5f, color)
        }
        if (renderScans && entity.has<SeekPlayer>()) {
            val seekComponent = entity.getComponent<SeekPlayer>()
            shapeDrawer.line(seekComponent.scanVectorStart, seekComponent.scanVectorEnd, Color.RED, .1f)
        }
    }
}
