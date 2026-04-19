package ai.arena

/**
 * Converts a SimulationResult into a single fitness score.
 * Higher is better. Weights are tunable without recompiling via [Weights].
 *
 * Formula:
 *   score = damageDealtToPlayer * damageWeight
 *         + (500 if playerKilled)
 *         + enemySurvivalTime * survivalWeight
 *         - damageTakenByEnemy * damageTakenPenalty
 *         - timeToFirstContact * aggressionBonus
 *         + proximityBonus (closer approach → better)
 */
object FitnessFunction {
    data class Weights(
        val damageDealt: Float = 2f,
        val playerKillBonus: Float = 500f,
        val survivalTime: Float = 0.5f,
        val damageTakenPenalty: Float = 0.3f,
        val aggressionBonus: Float = 1.0f,
        val proximityBonus: Float = 5f
    )

    var weights = Weights()

    fun score(result: SimulationResult): Float {
        if (result == SimulationResult.EMPTY) return 0f
        val w = weights
        val proximity = if (result.closestApproachToPlayer < Float.MAX_VALUE)
            (20f - result.closestApproachToPlayer).coerceAtLeast(0f) * w.proximityBonus
        else 0f
        val firstContact = if (result.timeToFirstContact < Float.MAX_VALUE)
            -result.timeToFirstContact * w.aggressionBonus
        else -100f

        return result.damageDealtToPlayer * w.damageDealt +
               (if (result.playerKilled) w.playerKillBonus else 0f) +
               result.enemySurvivalTime * w.survivalTime -
               result.damageTakenByEnemy * w.damageTakenPenalty +
               firstContact +
               proximity
    }

    /** Average score from multiple runs of the same tree (reduces noise). */
    fun averageScore(results: List<SimulationResult>): Float {
        if (results.isEmpty()) return 0f
        return results.sumOf { score(it).toDouble() }.toFloat() / results.size
    }
}
