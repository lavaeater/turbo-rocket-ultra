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

    fun nextItem() {
        selectedIndex++
        fixSelectedIndex()
        orderItems()
    }

    fun previousItem() {
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

        val itemsToDisplay = orderedItems.takeLast(visibleItemCount)

        val farLeft = vec2(visibleItemCount / 2 * -offset.x) //far left is number of items / 2 times offset
        val farRight = vec2(visibleItemCount / 2 * offset.x)

        for ((xIndex, item) in itemsToDisplay.withIndex()) {
            for((yIndex, element) in elements.withIndex()) {
                var actualScale = 0.5f
                var rightCount = 0
                var leftCount = 0
                if(xIndex == itemsToDisplay.lastIndex) {
                    element.position.set(0f, 0f)
                    actualScale = 1f
                } else {
                    if(xIndex % 2 == 0) {
                        farLeft.set(farLeft.x + offset.x * leftCount, farLeft.y)
                        element.position.set(farLeft.x, offset.y * yIndex)
                        leftCount++
                    } else {
                        farRight.set(farRight.x - offset.x * rightCount, farRight.y)
                        element.position.set(farRight.x, offset.y * yIndex)
                        rightCount++
                    }
                    actualScale = 0.5f
                }

                element.parent = this
                element.currentItem = item
                element.render(batch, delta, actualScale * scale, debug)
            }
        }
    }
}