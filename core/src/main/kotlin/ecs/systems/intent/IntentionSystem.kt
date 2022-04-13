package ecs.systems.intent

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.OnScreenComponent
import ecs.components.graphics.SpriteComponent
import ecs.components.intent.IntendsTo
import ecs.components.intent.IntentComponent
import ecs.components.player.BuildModeComponent
import ecs.components.player.Buildable
import ecs.systems.graphics.CompassDirection
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.remove
import ktx.ashley.with
import ktx.math.vec2
import map.grid.GridMapSection
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
import map.grid.GridMapSection.Companion.tileScale
import physics.*

class IntentionSystem: IteratingSystem(allOf(IntentComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        when(entity.intent()) {
            IntendsTo.ToggleBuildMode -> toggleBuildMode(entity)
            IntendsTo.DoNothing -> {}
            IntendsTo.Build -> build(entity)
        }
        entity.remove<IntentComponent>()
    }

    private fun leaveBuildMode(entity:Entity) {
        TODO("Not yet implemented")
    }

    private fun build(entity: Entity) {
        TODO("Not yet implemented")
    }

    private fun toggleBuildMode(entity: Entity) {
        if(entity.isBuilding()) {
            entity.remove<BuildModeComponent>()
        } else {
            entity.addComponent<BuildModeComponent> {  }
            val builderTransform = entity.transform()
            val builderPosition  = builderTransform.position
            val control = entity.playerControl()
            entity.build().buildCursorEntity = engine.entity {
                with<CalculatedPositionComponent> {
                    calculate = {
                        val cursorOffset = CompassDirection.directionOffsets[control.compassDirection]!!
                        calcPos.x = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth) + scaledWidth
                        calcPos.y = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight) + scaledHeight
                        calcPos
                    }
                }
                with<TransformComponent>()
                with<OnScreenComponent>()
                with<SpriteComponent> {
                    sprite = entity.build().buildables.selectedItem.sprite
                    scale = 4f
                }
            }
        }
    }
}

class CalculatedPositionComponent : Component, Pool.Poolable {
    val calcPos = vec2()
    var calculate: () -> Vector2 = {Vector2.Zero.cpy()}
    override fun reset() {
        calculate = { Vector2.Zero.cpy() }
        calcPos.setZero()
    }
}

class CalculatePositionSystem : IteratingSystem(allOf(CalculatedPositionComponent::class, TransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        transform.position.set(entity.getCalculatedPosition())
    }
}