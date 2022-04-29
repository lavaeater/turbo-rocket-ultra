package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.getComponent

class UpdatePlayerStatsSystem: IteratingSystem(
    allOf(PlayerComponent::class, WeaponComponent::class, InventoryComponent::class).get()
) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val player = entity.getComponent<PlayerComponent>().player
        val inventoryComponent = entity.getComponent<InventoryComponent>()
        val weaponComponent = entity.getComponent<WeaponComponent>()
        player.ammoLeft = weaponComponent.currentGun.ammoRemaining
        player.totalAmmo = inventoryComponent.ammo[weaponComponent.currentGun.ammoType]!!
    }
}

class WeaponChangeAndReloadSystem: IteratingSystem(
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
            controlComponent.setNewGun(weaponComponent.currentGun.rof / 60f)
        }
        if(controlComponent.needsReload) {
            // reload needs a cooldown and different ways of doing
            // depending on type of weapon
            controlComponent.needsReload = false
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            val gun = weaponComponent.currentGun
            val ammoType = gun.ammoType
            if(inventoryComponent.ammo.containsKey(ammoType)) {
                var ammoLeft = inventoryComponent.ammo[ammoType]!!
                if(ammoLeft > gun.ammoCap) {
                    gun.ammoRemaining = gun.ammoCap
                    ammoLeft -= gun.ammoCap
                } else {
                    gun.ammoRemaining = ammoLeft
                    ammoLeft = 0
                }
                inventoryComponent.ammo[ammoType] = ammoLeft
            }
        }
    }
}