package ai.tasks.leaf

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

open class PositionStorageComponent : Component, Pool.Poolable {
    val positions = mutableListOf<Vector2>()
    override fun reset() {
        positions.clear()
    }

}