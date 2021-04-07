package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

open class CollectionContainerElement<T:Any>(
    private val collection: List<T>,
    private val elements: List<BoundElement<T, *>>,
    val offset: Vector2 = vec2(200f, 20f),
    position: Vector2 = vec2(),
    parent: AbstractElement? = null) : AbstractElement(position, parent = parent) {

    override fun render(batch: Batch, delta:Float, debug: Boolean) {
        super.render(batch, delta, debug)
        for((xIndex, item) in collection.withIndex()) { //Move horizontally
            for((yIndex, element) in elements.withIndex()) { //Move vertically
                element.position.set(offset.x * xIndex, offset.y * yIndex)
                element.parent = this
                element.currentItem = item
                element.render(batch, delta, debug)
            }
        }
    }
}