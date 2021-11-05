package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.getComponent

class WeaponChangeSystem: IteratingSystem(
    allOf(
        WeaponComponent::class,
        InventoryComponent::class,
        PlayerControlComponent::class
    ).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if(controlComponent.needToChangeGun) {
            controlComponent.needToChangeGun = false
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            weaponComponent.currentGun = inventoryComponent.guns.nextItem()
        }
    }
}