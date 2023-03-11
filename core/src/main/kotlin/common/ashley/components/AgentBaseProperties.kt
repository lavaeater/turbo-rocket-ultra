package common.ashley.components

data class AgentBaseProperties(
    val meleeDistance: Float = 100f,
    val rotationSpeed: Float = 45f,
    val flock: Boolean = true,
    val fieldOfView: Float = 90f,
    val viewDistance: Float = 30f,
    val speed: Float = 5f,
    val rushSpeed: Float = 10f,
    val attackSpeed: Float = 0.5f,
    val meleeDamageRange: ClosedFloatingPointRange<Float> = (5f..15f)
) {
    companion object {
        val reusableBaseProperties = AgentBaseProperties()
    }
}