package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import features.weapons.AmmoType
import features.weapons.Weapon

class InventoryComponent: Component, Pool.Poolable {
    val weapons get() = InventoryComponent.weapons
    val ammo get() = InventoryComponent.ammo
    override fun reset() {
        weapons.clear()
        ammo.clear()
    }
    //if we want communal inventory
    companion object {
        val weapons = selectedItemListOf<Weapon>()
        val ammo = mutableMapOf<AmmoType, Int>()
    }
}