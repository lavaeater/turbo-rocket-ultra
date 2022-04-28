package ai.builders

interface Builder<out T> {
    fun build(): T
}