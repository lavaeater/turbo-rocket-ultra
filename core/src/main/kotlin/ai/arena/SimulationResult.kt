package ai.arena

data class SimulationResult(
    val enemySurvivalTime: Float,
    val damageDealtToPlayer: Float,
    val damageTakenByEnemy: Float,
    val playerKilled: Boolean,
    val closestApproachToPlayer: Float,
    val timeToFirstContact: Float
) {
    companion object {
        val EMPTY = SimulationResult(0f, 0f, 0f, false, Float.MAX_VALUE, Float.MAX_VALUE)
    }
}
