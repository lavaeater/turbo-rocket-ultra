package animation

interface Builder<out T> {
    fun build(): T
}