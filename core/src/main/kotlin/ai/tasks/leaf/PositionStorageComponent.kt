package ai.tasks.leaf

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

open class StorageComponent<T>: Component, Pool.Poolable {
    val storage = mutableListOf<T>()
    override fun reset() {
        storage.clear()
    }

}

open class PositionStorageComponent : StorageComponent<Vector2>()