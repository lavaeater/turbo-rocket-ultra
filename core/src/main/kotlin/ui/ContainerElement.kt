package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

open class ContainerElement(position: Vector2, parent: AbstractElement? = null): AbstractElement(position, parent = parent) {
    open val childActors = mutableListOf<AbstractElement>()

    override val bounds: Rectangle
        get() = Rectangle(
            childActors.minOf { it.actualPosition.x },
            childActors.minOf { it.actualPosition.y },
            childActors.maxOf { it.actualPosition.x } - childActors.minOf { it.actualPosition.x } + childActors.maxOf { it.bounds.width },
            childActors.maxOf { it.actualPosition.y } - childActors.minOf { it.actualPosition.y } + childActors.maxOf { it.bounds.height })

    fun addChild(child: AbstractElement) {
        child.parent = this
        childActors.add(child)
    }

    fun removeChild(child: AbstractElement) {
        val index = childActors.indexOf(child)
        if(index != -1 ) {
            val c = childActors.removeAt(index)
            c.parent = null
        }
    }

    override fun render(batch: Batch, delta: Float, debug: Boolean) {
        super.render(batch, delta, debug)
        for (child in childActors) {
            child.render(batch, delta, debug)
        }
    }
}