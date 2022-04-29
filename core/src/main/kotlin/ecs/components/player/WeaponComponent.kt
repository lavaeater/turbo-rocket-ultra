package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ecs.systems.player.SelectedItemList
import features.weapons.WeaponDefinition


class WeaponComponent: Component, Pool.Poolable {
    var reloading = false
    var reloadCoolDown: Float = WeaponDefinition.weapons.first().reloadDelay
    var currentWeapon = WeaponDefinition.weapons.first().getWeapon()
    override fun reset() {
        currentWeapon = WeaponDefinition.weapons.first().getWeapon()
        reloadCoolDown = WeaponDefinition.weapons.first().reloadDelay
        //No-op
    }
}