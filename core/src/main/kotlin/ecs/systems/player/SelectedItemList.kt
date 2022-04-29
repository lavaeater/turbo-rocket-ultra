package ecs.systems.player


fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList<T>()
    items.forEach { list.add(it) }
    return list
}

class SelectedItemList<T> : ArrayList<T>() {
    var selectedIndex: Int = 0
        private set(value) {
            field = when {
                value < 0 -> this.lastIndex
                value > this.lastIndex -> 0
                else -> value
            }
        }
    val selectedItem get () = this[selectedIndex]
    fun nextItem() : T {
        selectedIndex++
        return selectedItem
    }
    fun previousItem() : T {
        selectedIndex--
        return selectedItem
    }
}