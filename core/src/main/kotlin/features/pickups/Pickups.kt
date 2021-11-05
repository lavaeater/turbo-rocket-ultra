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

class LootTable(
    override val contents: List<ILoot>,
    override var probability: Float,
    override val count: Int,
    override val unique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (ILoot) -> Unit = {},
    override val hit: (ILoot) -> Unit = {},
    override val postResultEvaluation: (ILoot) -> Unit = {}
) : ILootTable {
    override val result: List<ILoot> get() {
        return emptyList()
    }

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
    probability: Float,
    unique: Boolean = false,
    always: Boolean = false,
    enabled: Boolean = true,
    preResultEvaluation: (ILoot) -> Unit = {},
    hit: (ILoot) -> Unit = {},
    postResultEvaluation: (ILoot) -> Unit = {}
) : LootValue<Object?>(
    null,
    probability,
    unique,
    always,
    enabled,
    preResultEvaluation,
    hit,
    postResultEvaluation
)

open class LootValue<T>(
    override val lootValue: T,
    override var probability: Float,
    override val unique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (ILoot) -> Unit = {},
    override val hit: (ILoot) -> Unit = {},
    override val postResultEvaluation: (ILoot) -> Unit = {}
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