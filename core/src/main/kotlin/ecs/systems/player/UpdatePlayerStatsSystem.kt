package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.getComponent
import physics.has

class UpdatePlayerStatsSystem : IteratingSystem(
    allOf(PlayerComponent::class, WeaponComponent::class, InventoryComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val player = entity.getComponent<PlayerComponent>().player
        val inventoryComponent = entity.getComponent<InventoryComponent>()
        if(entity.has<WeaponComponent>()) {
            val weaponComponent = entity.getComponent<WeaponComponent>()
            player.currentWeapon = weaponComponent.currentWeapon.name
            player.ammoLeft = weaponComponent.currentWeapon.ammoRemaining
            player.totalAmmo = if(inventoryComponent.ammo.containsKey(weaponComponent.currentWeapon.ammoType)) inventoryComponent.ammo[weaponComponent.currentWeapon.ammoType]!! else 0
        }
    }
}