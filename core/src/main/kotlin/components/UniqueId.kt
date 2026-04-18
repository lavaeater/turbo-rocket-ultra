package components

object UniqueId {
    private var id = 0
    fun next(): Int {
        id++
        return id
    }
}