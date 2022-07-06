package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import eater.ecs.components.TransformComponent
import eater.injection.InjectionContext.Companion.inject
import ecs.components.towers.TargetInRange
import ecs.components.towers.TowerComponent
import ktx.ashley.allOf
import ktx.graphics.use
import eater.physics.getComponent
import eater.physics.has
import tru.Assets


class TowerDebugSystem :
    IteratingSystem(
        allOf(
            TransformComponent::class,
            TowerComponent::class
        ).get()
    ) {

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.has<TargetInRange>()) {
            val transform = entity.getComponent<TransformComponent>()
            val targetPosition = entity.getComponent<TargetInRange>().targetPosition
            val aimTarget = entity.getComponent<TargetInRange>().aimTarget

            shapeDrawer.line(transform.position, targetPosition, Color.BLUE, 0.2f)
            shapeDrawer.line(transform.position, aimTarget, Color.RED, 0.05f)
        }
    }

}