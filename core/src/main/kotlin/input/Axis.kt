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
            1 to LeftY,
            2 to RightX,
            3 to RightY
        )

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