package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DamageEffectComponent: Component, Pool.Poolable {
    /*
    I want the area effect to be... what?

    A bunch of circles perhaps? that spill outward somehow?
     */


    override fun reset() {
    }
}

class MolotovComponent: Component, Pool.Poolable {

    override fun reset() {
    }
}

class BulletComponent: Component, Pool.Poolable {

    var damage = 0
    override fun reset() {
        damage = 0
    }
}