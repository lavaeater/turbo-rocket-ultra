package ai.tasks

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import factories.engine
import factories.factsOfTheWorld
import turbofacts.NewFactsOfTheWorld

abstract class EntityTask : LeafTask<Entity>() {
    protected val engine: Engine by lazy { engine() }
    protected val factsOfTheWorld: NewFactsOfTheWorld by lazy { factsOfTheWorld() }

    protected val entity: Entity get() = `object`
    protected var firstRun = true

    override fun reset() {
        super.reset()
        firstRun = true
    }
}