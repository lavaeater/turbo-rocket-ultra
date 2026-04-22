package systems.intent

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.TransformComponent
import physics.addComponent
import components.graphics.RenderableComponent
import components.graphics.TextureRegionComponent
import components.intent.CalculatedPositionComponent
import components.intent.IntendsTo
import components.intent.IntentComponent
import components.player.BuildModeComponent
import ecs.components.graphics.RenderableType
import systems.graphics.CompassDirection
import systems.tileWorldX
import systems.tileWorldY
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
                with<TextureRegionComponent> {
                    textureRegion = entity.buildModal().buildables.selectedItem.textureRegion
                    scale = 4f
                    updateTextureRegion = { textureRegion = entity.buildModal().buildables.selectedItem.textureRegion }
                }
            }
        }
    }
}