package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.WeaponComponent
import ecs.components.player.WeaponLaserComponent
import ktx.ashley.allOf
import physics.getComponent

/**
 * We need a cool-down system, which determines the rate of fire.
 *
 * This means you can always shoot if the weapon is cool.
 */

class WeaponLaserSystem : IteratingSystem(
    allOf(
        WeaponLaserComponent::class,
        TransformComponent::class,
        PlayerControlComponent::class,
        WeaponComponent::class
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weaponLaser = entity.getComponent<WeaponLaserComponent>().weaponlaser
        val transformComponent = entity.getComponent<TransformComponent>()
        val playerControlComponent = entity.getComponent<PlayerControlComponent>()
        val weaponComponent = entity.getComponent<WeaponComponent>()

        val weaponAcc = weaponComponent.currentWeapon.spreadOrMeleeRangeOrArea
        val playerPosition = transformComponent.position
        val aimVector = playerControlComponent.aimVector
        if(playerControlComponent.aiming) {
            weaponLaser.isActive = true
            weaponLaser.setPosition(playerPosition.x + aimVector.x, playerPosition.y + aimVector.y)
            weaponLaser.coneDegree = weaponAcc
            weaponLaser.direction = aimVector.angleDeg()
        } else {
            weaponLaser.isActive = false
        }
    }

}