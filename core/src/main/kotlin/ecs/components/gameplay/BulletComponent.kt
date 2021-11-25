package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.CoolDownComponent

class BurningComponent : CoolDownComponent() {
    var damageRange = 5..15
    override fun reset() {
        //Burn for this amount of time
        coolDownRange = 3f..8f
        super.reset()
    }
}

class DamageEffectComponent: Component, Pool.Poolable {
    override fun reset() {
    }
}

class DestroyAfterCoolDownComponent: CoolDownComponent() {
}

class MolotovComponent: Component, Pool.Poolable {

    override fun reset() {
    }
}

class BulletComponent: Component, Pool.Poolable {

    var damage = 0f
    override fun reset() {
        damage = 0f
    }
}