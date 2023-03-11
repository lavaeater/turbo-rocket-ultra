package common.core

class SelectedItemList<T>(val listUpdatedCallback: (Int, T)-> Unit, items: List<T>) : ArrayList<T>() {
    init {
        this.addAll(items)
    }

    val withSelectedItemFirst: List<T> get() {
        val item = get(selectedIndex)
        this.sortBy { it.toString() }
        val newList = mutableListOf<T>()
        var indexToAdd = indexOf(item)
        for(i in 0 until this.size) {
            indexToAdd = when {
                indexToAdd < 0 -> lastIndex
                indexToAdd > lastIndex -> 0
                else -> indexToAdd
            }
            newList.add(this[indexToAdd])
            indexToAdd += 1
        }
        return newList
    }

    fun getNItemsBeforeAndAfterIndex(n: Int, index: Int): List<T> {
        val range = ((index-n)..(index+n)).map { mapIndex(it) }
        return range.map { get(it) }
    }

    private fun mapIndex(index: Int):Int {
        return if(index < 0)
            lastIndex + index + 1
        else if(index > lastIndex)
            index - size
        else index
    }

    private var selectedIndex: Int = 0
        private set(value) {
            field = when {
                value < 0 -> this.lastIndex
                value > this.lastIndex -> 0
                else -> value
            }
            listUpdatedCallback(value, selectedItem)
        }
    var selectedItem get () = this[selectedIndex]
        set(value) {
            val index = indexOf(value)
            selectedIndex = index
        }
    fun nextItem() : T {
        selectedIndex++
        return selectedItem
    }
    fun previousItem() : T {
        selectedIndex--
        return selectedItem
    }
}
