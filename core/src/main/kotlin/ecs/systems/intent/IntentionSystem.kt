package ecs.systems.intent

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.components.TransformComponent
import eater.physics.addComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.graphics.RenderableType
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

class IntentionSystem : IteratingSystem(allOf(IntentComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        when (entity.intent()) {
            IntendsTo.ToggleBuildMode -> toggleBuildMode(entity)
            IntendsTo.DoNothing -> {}
            IntendsTo.Build -> build(entity)
            IntendsTo.SelectNextWeapon -> handleWeaponChange(entity, true)
            IntendsTo.SelectPreviousWeapon -> handleWeaponChange(entity, false)
            IntendsTo.Reload -> handleReload(entity)
        }
        entity.remove<IntentComponent>()
    }

    private fun handleReload(entity: Entity) {
        if(entity.isReloading())
            return
        else {
            entity.startReloading()
        }
    }

    private fun handleWeaponChange(entity: Entity, forwards: Boolean) {
        val newWeapon =
            if (forwards) entity.inventory().weapons.nextItem() else entity.inventory().weapons.previousItem()
        entity.weaponEntity().weapon().currentWeapon = newWeapon
        entity.playerControl().setNewGun(newWeapon.rof / 60f)
    }

    private fun build(entity: Entity) {
        if (entity.inBuildMode()) {
            val buildComponent = entity.buildModal()
            val cursorEntity = buildComponent.buildCursorEntity!!
            buildComponent.buildables.selectedItem.buildIt(cursorEntity.transform().position)
        }
    }

    private fun toggleBuildMode(entity: Entity) {
        if (entity.inBuildMode()) {
            val bc = entity.buildModal()
            engine.removeEntity(bc.buildCursorEntity)
            entity.remove<BuildModeComponent>()
        } else {
            entity.addComponent<BuildModeComponent> { }
            val builderTransform = entity.transform()
            val builderPosition = builderTransform.position
            val control = entity.playerControl()
            entity.buildModal().buildCursorEntity = engine.entity {
                with<CalculatedPositionComponent> {
                    calculate = {
                        val cursorOffset = CompassDirection.directionOffsets[control.compassDirection]!!
                        calcPos.x = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth) + scaledWidth / 2
                        calcPos.y = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight) + scaledHeight / 2
                        calcPos
                    }
                }
                with<TransformComponent>()
                with<RenderableComponent> {
                    renderableType = RenderableType.Sprite
                }
                with<SpriteComponent> {
                    sprite = entity.buildModal().buildables.selectedItem.sprite
                    scale = 4f
                    updateSprite = { sprite = entity.buildModal().buildables.selectedItem.sprite }
                }
            }
        }
    }
}