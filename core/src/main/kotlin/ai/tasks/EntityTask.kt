package ai.tasks

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import factories.engine
import factories.factsOfTheWorld
import injection.Context.inject
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.Assets
import eater.turbofacts.TurboFactsOfTheWorld

abstract class EntityTask : LeafTask<Entity>() {
    @delegate: Transient
    protected val engine: Engine by lazy { engine() }
    @delegate: Transient
    protected val factsOfTheWorld: TurboFactsOfTheWorld by lazy { factsOfTheWorld() }

    protected val entity: Entity get() = `object`
    protected var firstRun = true

    protected val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun reset() {
        super.reset()
        firstRun = true
    }

    override fun resetTask() {
        super.resetTask()
        firstRun = true
    }
}