package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

open class Carousel<T:Any>(items: List<T>,
                           elements: List<BoundElement<T, *>>,
                           offset: Vector2 = vec2(200f, 20f),
                           position: Vector2 = vec2(),
                           parent: AbstractElement? = null,
                           val visibleItemCount: Int = 3
) : CollectionContainerElement<T>(items, elements, offset, position, parent) {

    var selectedIndex = items.size / 2
    private val workList = mutableListOf<T>()
    private val orderedItems get() = workList.reversed()

    init {
        orderItems()
    }

    fun orderItems() {
        workList.clear()
        for(index in items.indices) {
            val actualIndex = calculateActualIndex(index)
            workList.add(items[actualIndex])
        }
    }

    private fun nextItem() {
        selectedIndex++
        fixSelectedIndex()
        orderItems()
    }

    private fun previousItem() {
        selectedIndex--
        fixSelectedIndex()
        orderItems()
    }

    private fun fixSelectedIndex() {
        if(selectedIndex < 0)
            selectedIndex = items.size - 1

        if(selectedIndex >= items.size)
            selectedIndex = 0
    }


    private fun calculateActualIndex(index: Int): Int {
        var newIndex = selectedIndex + index
        if(newIndex > items.lastIndex) {
           newIndex -= (items.lastIndex + selectedIndex)
        }
        return newIndex
    }


    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        if (debug)
            renderBounds()
        for ((xIndex, item) in orderedItems.withIndex()) {
            for((yIndex, element) in elements.withIndex()) {
                element.position.set(offset.x * xIndex, offset.y * yIndex)
                element.parent = this
                element.currentItem = item
                val actualScale = if(item == orderedItems.last()) 1f else 0.5f
                element.render(batch, delta, actualScale * scale, debug)
            }
        }
    }
}