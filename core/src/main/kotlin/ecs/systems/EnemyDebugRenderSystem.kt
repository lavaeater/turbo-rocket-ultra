package ecs.systems

import ai.enemy.EnemyState
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import ecs.components.ai.Amble
import ecs.components.ai.AttackPlayer
import ecs.components.ai.ChasePlayer
import ecs.components.ai.SeekPlayer
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.hasComponent
import tru.Assets

class EnemyDebugRenderSystem(private val renderStates: Boolean = false, private val renderScans: Boolean = false) : IteratingSystem(
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
            val enemyComponent = entity[enemyMapper]!!
            if(renderStates) {
                var color = Color.GREEN
                when {
                    entity.hasComponent<Amble>() -> color = Color.GREEN
                    entity.hasComponent<SeekPlayer>() -> color = Color.CYAN
                    entity.hasComponent<ChasePlayer>() -> color = Color.YELLOW
                    entity.hasComponent<AttackPlayer>() -> color = Color.RED
                }
                shapeDrawer.filledCircle(entity[transformMapper]!!.position, 5f, color)
            }
            if(renderScans && enemyComponent.state == EnemyState.Seeking)
                shapeDrawer.line(enemyComponent.scanVectorStart, enemyComponent.scanVectorEnd, Color.RED, .1f)
        }
    }
}