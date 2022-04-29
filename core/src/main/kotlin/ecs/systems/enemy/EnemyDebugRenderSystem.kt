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
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.getComponent
import physics.hasComponent
import tru.Assets

class EnemyDebugRenderSystem(
    private val renderStates: Boolean = false,
    private val renderScans: Boolean = true) : IteratingSystem(
    allOf(
        EnemyComponent::class,
        TransformComponent::class).get()) {

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val enemyMapper = mapperFor<EnemyComponent>()
    private val transformMapper = mapperFor<TransformComponent>()
    private val shapeDrawer by lazy { Assets.shapeDrawer }


    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.has(enemyMapper)) {
            if(renderStates) {
                var color = Color.GREEN
                when {
                    entity.hasComponent<Investigate>() -> color = Color.PURPLE
                    entity.hasComponent<Amble>() -> color = Color.GREEN
                    entity.hasComponent<SeekPlayer>() -> color = Color.CYAN
                    entity.hasComponent<ChasePlayer>() -> color = Color.YELLOW
                    entity.hasComponent<AttackPlayer>() -> color = Color.RED
                }
                shapeDrawer.filledCircle(entity[transformMapper]!!.position, 5f, color)
            }
            if(renderScans && entity.hasComponent<SeekPlayer>()) {
                val seekComponent = entity.getComponent<SeekPlayer>()
                shapeDrawer.line(seekComponent.scanVectorStart, seekComponent.scanVectorEnd, Color.RED, .1f)
            }
        }
    }
}