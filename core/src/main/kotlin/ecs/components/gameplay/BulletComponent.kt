package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player
import ecs.components.ai.CoolDownComponent

class DamageEffectComponent: Component, Pool.Poolable {
    lateinit var player: Player
    override fun reset() {
    }
}

class DestroyAfterCoolDownComponent: CoolDownComponent() {
}

class MolotovComponent: Component, Pool.Poolable {
    lateinit var player: Player
    override fun reset() {
    }
}

class BulletComponent: Component, Pool.Poolable {
    lateinit var player: Player
    var damage = 0f
    override fun reset() {
        damage = 0f
    }
}