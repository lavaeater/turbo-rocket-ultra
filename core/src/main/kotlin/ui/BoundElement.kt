package ui

import com.badlogic.gdx.math.Vector2

open class BoundElement<T: Any,V>(val valueFunc: (T) -> V, position: Vector2, parent: AbstractElement? = null) : AbstractElement(position, parent = parent) {
    lateinit var currentItem: T
}

