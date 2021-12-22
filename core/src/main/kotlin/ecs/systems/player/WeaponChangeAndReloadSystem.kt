package ecs.systems.player

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.InventoryComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.WeaponComponent
import features.weapons.ReloadType
import injection.Context.inject
import input.InputIndicator
import ktx.ashley.allOf
import physics.getComponent

class WeaponChangeAndReloadSystem : IteratingSystem(
    allOf(
        WeaponComponent::class,
        InventoryComponent::class,
        PlayerControlComponent::class
    ).get()
) {

    private val audioPlayer by lazy { inject<AudioPlayer>()}

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if (controlComponent.needToChangeGun != InputIndicator.Neutral) {
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            weaponComponent.currentWeapon = if(controlComponent.needToChangeGun == InputIndicator.Next) inventoryComponent.weapons.nextItem() else inventoryComponent.weapons.previousItem()
            controlComponent.setNewGun(weaponComponent.currentWeapon.rof / 60f)
            controlComponent.needToChangeGun = InputIndicator.Neutral
        }
        if (controlComponent.reloadStarted) {
            //This means we have initiated a reload, now we can check weaponcomponenet etc.
            // reload needs a cooldown and different ways of doing
            // depending on type of weapon
            val inventoryComponent = entity.getComponent<InventoryComponent>()
            val weaponComponent = entity.getComponent<WeaponComponent>()
            val gun = weaponComponent.currentWeapon

            val ammoType = gun.ammoType
            val reloadSound = gun.audio["reload"]!!
            if (inventoryComponent.ammo.containsKey(ammoType) && inventoryComponent.ammo[ammoType]!! > 0) {
                weaponComponent.reloadCoolDown -= deltaTime
                if (weaponComponent.reloadCoolDown <= 0f) {
                    controlComponent.resetSound(Sfx.outofAmmo)
                    when (gun.reloadType) {
                        ReloadType.SingleShot -> {
                            audioPlayer.playSound(reloadSound)
                            gun.ammoRemaining += 1
                            inventoryComponent.ammo[ammoType] = inventoryComponent.ammo[ammoType]!! - 1
                            if (gun.ammoRemaining == gun.ammoCap || inventoryComponent.ammo[ammoType]!! <= 0) {
                                controlComponent.reloadStarted = false
                            }
                        }
                        ReloadType.EntireMag -> {
                            audioPlayer.playSound(reloadSound)
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
                        ReloadType.MeleeWeapon -> {
                            //This is no-op because melee weapons don't need reloading
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