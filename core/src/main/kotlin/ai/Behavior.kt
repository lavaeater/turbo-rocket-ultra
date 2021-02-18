package ai

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import injection.Context.inject

/**
 * I want to control the npc using components in a system
 *
 * So perhaps the AI needs to be a system? Or how do we add or remove components to the
 * entity properly?
 *
 * Then we can simply have systems that are very limited and do limited things for all our behaviors
 * based on the entity having components or not.
 *
 * But when will we be done? I don't know!
 *
 */


abstract class EntityTask : LeafTask<Entity>() {
    protected val engine: Engine by lazy { inject()}
    protected val entity: Entity get() = `object`

    override fun copyTo(p0: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }
}
