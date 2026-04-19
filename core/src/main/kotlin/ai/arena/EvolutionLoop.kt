package ai.arena

import ai.behaviorTree.Mutator
import ai.behaviorTree.Tree
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree

/**
 * Drives one generation of evolution.
 *
 * Population lifecycle:
 *   1. Evaluate all candidates (run sim, score each)
 *   2. Sort by fitness descending
 *   3. Keep top [eliteCount] unchanged
 *   4. Fill the rest by mutating randomly-selected survivors (rank-weighted)
 *   5. Save generation to disk
 */
class EvolutionLoop(
    val populationSize: Int = 20,
    val eliteCount: Int = 4,
    val simDuration: Float = 60f,
    val runsPerCandidate: Int = 3,
    val saveDir: String = "arena",
    val onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }
) {
    var currentPopulation: Population? = null

    /** Seed the first generation from the game's default tree. */
    fun seedInitial(): Population {
        val seedTree = Tree.nowWithAttacks()
        val candidates = List(populationSize) { i ->
            if (i == 0) Candidate(seedTree)
            else Candidate(Mutator.getMutatedTree(seedTree))
        }
        return Population(0, candidates).also { currentPopulation = it }
    }

    /** Run one full generation. Blocking — call on a background thread in the UI. */
    fun runGeneration(population: Population): Population {
        // Evaluate
        val evaluated = population.candidates.mapIndexed { index, candidate ->
            val result = ArenaSimulation.evaluate(candidate.tree, simDuration, runsPerCandidate)
            onProgress(index + 1, population.candidates.size)
            Candidate(candidate.tree, FitnessFunction.score(result))
        }.sortedByDescending { it.score }

        // Elitism: keep top N unchanged
        val elites = evaluated.take(eliteCount)

        // Fill rest by mutating rank-weighted survivors
        val survivors = evaluated  // all sorted; pick from top half by preference
        val halfSize = (survivors.size / 2).coerceAtLeast(1)
        val offspring = List(populationSize - eliteCount) {
            // Weighted pick: first half of sorted list gets ~2× the weight
            val parentPool = if ((0..1).random() == 0) survivors.take(halfSize) else survivors
            val parent = parentPool.random()
            Candidate(Mutator.getMutatedTree(parent.tree))
        }

        val nextGen = Population(population.generation + 1, elites + offspring)
        nextGen.saveToFile(saveDir)
        currentPopulation = nextGen
        return nextGen
    }

    /** Convenience: run [generations] generations from scratch. */
    fun run(generations: Int): Population {
        var pop = currentPopulation ?: seedInitial()
        repeat(generations) { pop = runGeneration(pop) }
        return pop
    }
}
