package turbofacts

sealed class Factoid(val key: String) {
    sealed class Fact<T>(key: String, var value: T) : Factoid(key) {
        override fun toString(): String {
            return "$key: $value"
        }

        class BooleanFact(key: String, value: Boolean) : Fact<Boolean>(key, value)
        class IntFact(key: String, value: Int) : Fact<Int>(key, value)
        class StringFact(key: String, value: String) : Fact<String>(key, value)
        class FloatFact(key: String, value: Float) : Fact<Float>(key, value)
        class StringListFact(key: String, value: MutableList<String>) : Fact<MutableList<String>>(key, value) {
            override fun toString(): String {
                return "$key contains ${value.joinToString("\n")}"
            }
        }
    }
}