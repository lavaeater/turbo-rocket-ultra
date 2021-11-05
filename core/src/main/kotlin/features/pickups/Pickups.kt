package features.pickups

import ktx.math.random

interface ILoot {
    var probability: Float
    val unique: Boolean
    val always: Boolean
    val enabled: Boolean
    val preResultEvaluation: (ILoot) -> Unit
    val hit: (ILoot) -> Unit
    val postResultEvaluation: (ILoot) -> Unit

}

interface ILootTable : ILoot {
    val count: Int
    val contents: List<ILoot>
    val result: List<ILoot>
}


interface ILootValue<T> : ILoot {
    val lootValue: T
}

class Loot(
    override var probability: Float,
    override val unique: Boolean,
    override val always: Boolean,
    override val enabled: Boolean,
    override val preResultEvaluation: (ILoot) -> Unit,
    override val hit: (ILoot) -> Unit,
    override val postResultEvaluation: (ILoot) -> Unit
) : ILoot

class NullValue(
    probability: Float, unique: Boolean, always: Boolean, enabled: Boolean,
    preResultEvaluation: (ILoot) -> Unit, hit: (ILoot) -> Unit, postResultEvaluation: (ILoot) -> Unit
) : LootValue<Object?>(
    probability,
    unique,
    always,
    enabled,
    preResultEvaluation, hit, postResultEvaluation, null
)

open class LootValue<T>(
    override var probability: Float,
    override val unique: Boolean,
    override val always: Boolean,
    override val enabled: Boolean,
    override val preResultEvaluation: (ILoot) -> Unit,
    override val hit: (ILoot) -> Unit,
    override val postResultEvaluation: (ILoot) -> Unit,
    override val lootValue: T
) : ILootValue<T>

object Randomizer {
    fun getRandomFloat(max: Float): Float {
        return (0f..max).random()
    }
    fun getRandomFloat(min: Float, max:Float): Float {
        return (min..max).random()
    }

    fun getRandomInt(max: Int):Int {
        return (0..max).random()
    }
    fun getRandomInt(min:Int, max:Int): Int {
        return (min..max).random()
    }
    fun rollDice(numberOfDice: Int, sidesPerDice: Int): List<Int> {
        return (0 until numberOfDice).map {
            (1..sidesPerDice).random()
        }.toList()
    }
    fun isPercentHit(percent: Int): Boolean {
        return (0..99).random() < percent
    }

}