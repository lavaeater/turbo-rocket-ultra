package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import gamestate.Player

object GunFrames {
    const val handGun = "handgun"
    const val spas12 = "spas-12"
    val gunFrames = mapOf(handGun to 11..11, spas12 to 10..10)
}

class WeaponComponent: Component, Pool.Poolable {
    var currentGun = GunFrames.handGun
    override fun reset() {
        currentGun = GunFrames.handGun
        //No-op
    }
}

class PlayerComponent: Component, Pool.Poolable {
    lateinit var player: Player
    override fun reset() {
        //No-op
    }
}