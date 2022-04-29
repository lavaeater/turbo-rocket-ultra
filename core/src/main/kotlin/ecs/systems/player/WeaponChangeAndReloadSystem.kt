package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.WeaponComponent
import features.weapons.ReloadType
import input.InputIndicator
import ktx.ashley.allOf
import physics.getComponent

class UpdatePlayerStatsSystem : IteratingSystem(
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

class WeaponChangeAndReloadSystem : IteratingSystem(
    allOf(
        WeaponComponent::class,
        InventoryComponent::class,
        PlayerControlComponent::class
    ).get()
) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if (controlComponent.needToChangeGun != InputIndicator.Neutral) {
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            weaponComponent.currentGun = if(controlComponent.needToChangeGun == InputIndicator.Next) inventoryComponent.guns.nextItem() else inventoryComponent.guns.previousItem()
            controlComponent.setNewGun(weaponComponent.currentGun.rof / 60f)
            controlComponent.needToChangeGun = InputIndicator.Neutral
        }
        if (controlComponent.reloadStarted) {
            //This means we have initiated a reload, now we can check weaponcomponenet etc.
            // reload needs a cooldown and different ways of doing
            // depending on type of weapon
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            val gun = weaponComponent.currentGun
            val ammoType = gun.ammoType
            if (inventoryComponent.ammo.containsKey(ammoType) && inventoryComponent.ammo[ammoType]!! > 0) {
                weaponComponent.reloadCoolDown -= deltaTime
                if (weaponComponent.reloadCoolDown <= 0f) {
                    when (gun.reloadType) {
                        ReloadType.SingleShot -> {
                            gun.ammoRemaining += 1
                            inventoryComponent.ammo[ammoType] = inventoryComponent.ammo[ammoType]!! - 1
                            if (gun.ammoRemaining == gun.ammoCap || inventoryComponent.ammo[ammoType]!! <= 0) {
                                controlComponent.reloadStarted = false
                            }
                        }
                        ReloadType.EntireMag -> {
                            var ammoLeft = inventoryComponent.ammo[ammoType]!!
                            if (ammoLeft > gun.ammoCap) {
                                gun.ammoRemaining = gun.ammoCap
                                ammoLeft -= gun.ammoCap
                            } else {
                                gun.ammoRemaining = ammoLeft
                                ammoLeft = 0
                            }
                            inventoryComponent.ammo[ammoType] = ammoLeft
                            controlComponent.reloadStarted = false
                        }
                    }
                    weaponComponent.reloadCoolDown = gun.reloadDelay
                }
            } else {
                controlComponent.reloadStarted = false
            }
        }
    }
}