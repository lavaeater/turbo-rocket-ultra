package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.utils.Pool

class BehaviorComponent : Component,Pool.Poolable {

    lateinit var tree: BehaviorTree<Entity>
    override fun reset() {
        tree.reset()
    }
}