package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class WeaponEntityComponent: Component, Pool.Poolable {
    //Is this shit? It might be shit
    lateinit var weaponEntity: Entity
    override fun reset() {

    }
}