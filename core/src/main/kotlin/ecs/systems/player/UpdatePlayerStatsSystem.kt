package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.WeaponEntityComponent
import ecs.components.player.doWeHaveAny
import ktx.ashley.allOf
import physics.getComponent
import physics.weapon
import physics.weaponEntity

class UpdatePlayerStatsSystem : IteratingSystem(
    allOf(PlayerComponent::class, WeaponEntityComponent::class, InventoryComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val player = entity.getComponent<PlayerComponent>().player
        val inventoryComponent = entity.getComponent<InventoryComponent>()
        val weaponComponent = entity.weaponEntity().weapon()
        player.currentWeapon = weaponComponent.currentWeapon.name
        player.ammoLeft = weaponComponent.currentWeapon.ammoRemaining
        player.totalAmmo =
            if (weaponComponent.currentWeapon.ammoType.doWeHaveAny()) inventoryComponent.ammo[weaponComponent.currentWeapon.ammoType]!! else 0
    }
}