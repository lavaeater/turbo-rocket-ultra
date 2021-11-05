package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import features.weapons.GunFrames

class WeaponComponent: Component, Pool.Poolable {
    var currentGun = GunFrames.handGun
    override fun reset() {
        currentGun = GunFrames.handGun
        //No-op
    }
}