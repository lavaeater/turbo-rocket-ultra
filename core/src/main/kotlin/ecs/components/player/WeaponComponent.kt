package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ecs.systems.player.SelectedItemList
import features.weapons.AmmoType
import features.weapons.Weapon
import features.weapons.WeaponDefinition

inline fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList<T>()
    items.forEach { list.add(it) }
    return list
}

class InventoryComponent: Component, Pool.Poolable {
    val weapons = selectedItemListOf<Weapon>()
    val ammo = mutableMapOf<AmmoType, Int>()
    override fun reset() {
        weapons.clear()
        ammo.clear()
    }
}

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