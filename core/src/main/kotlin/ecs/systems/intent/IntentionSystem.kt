package ecs.systems.intent

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.OnScreenComponent
import ecs.components.graphics.SpriteComponent
import ecs.components.intent.CalculatedPositionComponent
import ecs.components.intent.IntendsTo
import ecs.components.intent.IntentComponent
import ecs.components.player.BuildModeComponent
import ecs.systems.graphics.CompassDirection
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.remove
import ktx.ashley.with
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
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

    private fun build(entity: Entity) {
        if(entity.isBuilding()) {
            val buildComponent = entity.build()
            val cursorEntity = buildComponent.buildCursorEntity!!
            buildComponent.buildables.selectedItem.buildIt(cursorEntity.transform().position)
            engine.removeEntity(cursorEntity)
            entity.remove<BuildModeComponent>()
        }
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
                        calcPos.x = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth) + scaledWidth / 2
                        calcPos.y = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight) + scaledHeight / 2
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