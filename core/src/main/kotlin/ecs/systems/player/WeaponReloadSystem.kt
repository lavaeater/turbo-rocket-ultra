package ecs.systems.player

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.*
import features.weapons.ReloadType
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.*

class WeaponReloadSystem : IteratingSystem(
    allOf(
        WeaponEntityComponent::class,
        InventoryComponent::class,
        IsReloadingComponent::class
    ).get()
) {

    private val audioPlayer by lazy { inject<AudioPlayer>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val inventory = entity.inventory()
        val weaponComponent = entity.weaponEntity().weapon()
        val weapon = weaponComponent.currentWeapon
        val isReloading = entity.reloader()
        val ammoType = weapon.ammoType
        if (isReloading.reloadHasStarted) {

            isReloading.coolDown -= deltaTime
            if (isReloading.coolDown <= 0f) {
                val reloadSound = weapon.audio["reload"]!!
                when (weapon.reloadType) {
                    ReloadType.SingleShot -> {
                        //TODO: Change to my cool channel based audio stuff
                        reloadSound.play()
                        weapon.ammoRemaining += 1
                        inventory.ammo[ammoType] = inventory.ammo[ammoType]!! - 1
                        if (weapon.ammoRemaining == weapon.ammoCap || inventory.ammo[ammoType]!! <= 0) {
                            entity.remove<IsReloadingComponent>()
                        } else {
                            isReloading.reloadHasStarted = false
                        }
                    }
                    ReloadType.EntireMag -> {
                        reloadSound.play()
                        //TODO: Change to my cool channel based audio stuff
//                            audioPlayer.playSound(reloadSound)
                        var ammoLeft = inventory.ammo[ammoType]!!
                        if (ammoLeft > weapon.ammoCap) {
                            weapon.ammoRemaining = weapon.ammoCap
                            ammoLeft -= weapon.ammoCap
                        } else {
                            weapon.ammoRemaining = ammoLeft
                            ammoLeft = 0
                        }
                        inventory.ammo[ammoType] = ammoLeft
                        entity.remove<IsReloadingComponent>()
                    }
                    ReloadType.MeleeWeapon -> {
                        //This is no-op because melee weapons don't need reloading
                    }
                }
            }

        } else if (ammoType.doWeHaveAny()) {
            isReloading.coolDown = weapon.reloadDelay
            isReloading.reloadHasStarted = true
        }
    }
}