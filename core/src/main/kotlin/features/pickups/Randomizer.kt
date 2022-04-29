package features.pickups

import ktx.math.random

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