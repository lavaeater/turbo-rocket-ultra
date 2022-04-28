package ai.tasks

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import injection.Context

abstract class EntityTask : LeafTask<Entity>() {
    protected val engine: Engine by lazy { Context.inject() }
    protected val entity: Entity get() = `object`
}