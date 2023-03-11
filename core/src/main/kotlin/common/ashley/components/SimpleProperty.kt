package common.ashley.components

import com.badlogic.gdx.math.MathUtils

sealed class SimpleProperty(val propertyName: PropertyName) {
    class FloatProperty(name: PropertyName, current: Float = 100f, val min: Float = 0f, val max: Float = 100f): SimpleProperty(name) {
        var current = current
            set(value) {
                field = MathUtils.clamp(value, min, max)
            }
        val normalizedValue: Float
                get() = MathUtils.norm(min, max, MathUtils.clamp(current, 0f, max))
    }
}