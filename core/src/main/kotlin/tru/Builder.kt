package tru

interface Builder<out T> {
    fun build(): T
}