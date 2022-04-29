package input

sealed class Axis {
    object TriggerPull : Axis()
    object LeftX : Axis()
    object LeftY : Axis()
    object RightX : Axis()
    object RightY : Axis()
    object Unknown : Axis()
    companion object {

        val axisMap = mapOf(
            5 to TriggerPull,
            0 to LeftX,
            1 to LeftY
        )

        val axisToKeys: Map<Axis, Int> = axisMap.map { it.value to it.key }.toMap()

        fun valueOK(value: Float): Boolean {
            return value > 0.3f || value < -0.3f
        }

        val axesList get() = axisMap.values.toList()

        fun getAxis(axisCode: Int): Axis {
            return when (axisMap.containsKey(axisCode)) {
                true -> axisMap[axisCode]!!
                false -> Unknown
            }
        }
    }
}