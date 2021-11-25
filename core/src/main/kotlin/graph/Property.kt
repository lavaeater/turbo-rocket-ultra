package graph

import ktx.math.ImmutableVector2

sealed class Property {
    abstract val name: String

    sealed class GenericTypedProperty<T>(override val name: String, open var value:T):Property() {
        sealed class StringProperty(override val name: String, override var value: String) : GenericTypedProperty<String>(name, value)
        sealed class IntProperty(override val name: String, override var value: Int) : GenericTypedProperty<Int>(name, value)
        sealed class VectorProperty(override val name: String, override var value: ImmutableVector2) : GenericTypedProperty<ImmutableVector2>(name, value)
        sealed class BoolProperty(override val name: String, override var value: Boolean) : GenericTypedProperty<Boolean>(name, value)
    }
}