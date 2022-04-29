package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.CoolDownComponent
import ecs.systems.player.SelectedItemList
import features.weapons.WeaponDefinition

class IsReloadingComponent: CoolDownComponent() {
    var reloadHasStarted = false
    override fun reset() {
        reloadHasStarted = false
    }
}

class WeaponEntityComponent: Component, Pool.Poolable {
    //Is this shit? It might be shit
    lateinit var weaponEntity: Entity
    override fun reset() {

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