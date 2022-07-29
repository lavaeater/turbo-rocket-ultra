package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

open class Carousel<T : Any>(
    items: List<T>,
    elements: List<BoundElement<T, *>>,
    offset: Vector2 = vec2(200f, 20f),
    position: Vector2 = vec2(),
    parent: AbstractElement? = null,
    val visibleItemCount: Int = 3
) : CollectionContainerElement<T>(items, elements, offset, position, parent) {

    /*
    The carousel should work by using a simple list that is ordered
    from the beginning, and then a "window" to this list, starting at
    some index, then moving some items to the right.
     */

    var selectedIndex = 0
    val selectedItem get() = items[selectedIndex]

    fun nextItem() {
        selectedIndex++
        fixSelectedIndex()
    }

    fun previousItem() {
        selectedIndex--
        fixSelectedIndex()
    }

    private fun fixSelectedIndex() {
        if (selectedIndex < 0)
            selectedIndex = items.size - 1

        if (selectedIndex >= items.size)
            selectedIndex = 0
    }

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        if (debug)
            renderBounds()

        /*
        selected index is the starting item. Then we move one step right,
        per item, until we hit max, then we roll around to the start on 0
        and we stop when we hit max displayed items.
         */
        for (index in selectedIndex until selectedIndex + visibleItemCount) {
            var xIndex = index
            if (xIndex >= items.size) {
                xIndex -= items.size
            }
            for ((yIndex, element) in elements.withIndex()) {
                element.position.set(offset.x * xIndex, offset.y * yIndex)
                element.parent = this
                element.currentItem = items[xIndex]
                element.render(batch, delta, scale *.5f, debug)
            }
        }
    }
}