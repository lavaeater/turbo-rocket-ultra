package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ecs.systems.player.SelectedItemList
import features.weapons.AmmoType
import features.weapons.Gun
import features.weapons.GunDefinition
import features.weapons.GunFrames

inline fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList<T>()
    items.forEach { list.add(it) }
    return list
}

class InventoryComponent: Component, Pool.Poolable {
    val guns = selectedItemListOf<Gun>()
    val ammo = mutableMapOf<AmmoType, Int>()
    override fun reset() {
        guns.clear()
        ammo.clear()
    }
}

class WeaponComponent: Component, Pool.Poolable {
    var reloading = false
    var reloadCoolDown: Float = GunDefinition.guns.first().reloadDelay
    var currentGun = GunDefinition.guns.first().getGun()
    override fun reset() {
        currentGun = GunDefinition.guns.first().getGun()
        reloadCoolDown = GunDefinition.guns.first().reloadDelay
        //No-op
    }
}