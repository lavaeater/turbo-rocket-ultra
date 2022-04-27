package turbofacts

import audio.AudioPlayer
import ecs.components.AudioChannels
import ecs.systems.enemy.multiKey
import injection.Context.inject
import tru.Assets
import tru.getRandomSoundFor

@kotlinx.serialization.Serializable
sealed class Factoid(val key: String) {
    sealed class Fact<T>(key: String, var value: T) : Factoid(key) {
        override fun toString(): String {
            return "$key: $value"
        }

        class BooleanFact(key: String, value: Boolean) : Fact<Boolean>(key, value)
        class IntFact(key: String, value: Int) : Fact<Int>(key, value)
        class StringFact(key: String, value: String) : Fact<String>(key, value)
        class FloatFact(key: String, value: Float) : Fact<Float>(key, value)
        class StringListFact(key: String, value: MutableList<String>) : Fact<MutableList<String>>(key, value) {
            override fun toString(): String {
                return "$key contains ${value.joinToString("\n")}"
            }
        }
    }
}

class NewStoryManager {
    var needsChecking = true
    fun checkIfNeeded() {
        if (needsChecking) {
            needsChecking = false
            for (rule in NewFactTester.rules) {
                if (rule.checkRule()) {
                    rule.consequence(rule.criteria)
                }
            }
        }
    }
}

object NewFactTester {
    val rules = listOf(TurboRule().apply {
        this.criteria.add(Criterion.BooleanCriteria.Any.IsTrue("Enemy.*.ReachedWayPoint"))
        this.consequence = {
            //inject<AudioPlayer>().playNextIfEmpty(AudioChannels.simultaneous, Assets.newSoundEffects.getRandomSoundFor("zombies","groans"))
        }
    })

}

class TurboRule {
    val criteria = mutableListOf<Criterion>()
    fun checkRule(): Boolean {
        return criteria.all { it.checkRule() }
    }

    var consequence: (criteria: List<Criterion>) -> Unit = {}
}

sealed class Criterion(val factKey: String) {
    protected val facts by lazy { inject<FotW>() }
    abstract fun checkRule(): Boolean
    sealed class BooleanCriteria(factKey: String) : Criterion(factKey) {
        sealed class All(factKey: String) : BooleanCriteria(factKey) {
            class IsTrue(factKey: String) : All(factKey) {
                override fun checkRule(): Boolean {
                    val facts = facts.factsFor(factKey)
                    return facts.all { it is Factoid.Fact.BooleanFact && it.value }
                }
            }

            class IsFalse(factKey: String) : All(factKey) {
                override fun checkRule(): Boolean {
                    val facts = facts.factsFor(factKey)
                    return facts.all { it is Factoid.Fact.BooleanFact && !it.value }
                }
            }
        }

        sealed class Any(factKey: String) : BooleanCriteria(factKey) {
            class IsTrue(factKey: String) : Any(factKey) {
                override fun checkRule(): Boolean {
                    val facts = facts.factsFor(factKey)
                    return facts.any { it is Factoid.Fact.BooleanFact && it.value }
                }
            }

            class IsFalse(factKey: String) : Any(factKey) {
                override fun checkRule(): Boolean {
                    val facts = facts.factsFor(factKey)
                    return facts.any { it is Factoid.Fact.BooleanFact && !it.value }
                }
            }
        }

        sealed class Single(factKey: String) : BooleanCriteria(factKey) {
            class IsTrue(factKey: String) : Single(factKey) {
                override fun checkRule(): Boolean {
                    return facts.getBooleanFact(factKey).value
                }
            }

            class IsFalse(factKey: String) : Single(factKey) {
                override fun checkRule(): Boolean {
                    return !facts.getBooleanFact(factKey).value
                }
            }
        }

    }

    sealed class IntCriteria(factKey: String) : Criterion(factKey) {
        companion object {
            fun moreThanChecker(valueToCheck: Int, value: Int): Boolean {
                return valueToCheck > value
            }

            fun lessThanChecker(valueToCheck: Int, value: Int): Boolean {
                return valueToCheck < value
            }

            fun equalsChecker(valueToCheck: Int, value: Int): Boolean {
                return valueToCheck == value
            }
        }

        sealed class All(factKey: String) : IntCriteria(factKey) {
            companion object {
                fun lessThan(factKey: String, valueToCheck: Int): AllOperatorCriteria {
                    return AllOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
                }

                fun moreThan(factKey: String, valueToCheck: Int): AllOperatorCriteria {
                    return AllOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
                }

                fun equals(factKey: String, valueToCheck: Int): AllOperatorCriteria {
                    return AllOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
                }
            }

            class AllOperatorCriteria(factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
                All(factKey) {
                override fun checkRule(): Boolean {
                    return facts.factsFor(factKey).all { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
                }
            }
        }

        sealed class Any(factKey: String) : IntCriteria(factKey) {
            companion object {
                fun lessThan(factKey: String, valueToCheck: Int): AnyOperatorCriteria {
                    return AnyOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
                }

                fun moreThan(factKey: String, valueToCheck: Int): AnyOperatorCriteria {
                    return AnyOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
                }

                fun equals(factKey: String, valueToCheck: Int): AnyOperatorCriteria {
                    return AnyOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
                }
            }

            class AnyOperatorCriteria(factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
                Any(factKey) {
                override fun checkRule(): Boolean {
                    return facts.factsFor(factKey).any { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
                }
            }
        }

        sealed class Single(factKey: String) : IntCriteria(factKey) {
            companion object {
                fun lessThan(factKey: String, valueToCheck: Int): SingleOperatorCriteria {
                    return SingleOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
                }

                fun moreThan(factKey: String, valueToCheck: Int): SingleOperatorCriteria {
                    return SingleOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
                }

                fun equals(factKey: String, valueToCheck: Int): SingleOperatorCriteria {
                    return SingleOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
                }
            }

            class SingleOperatorCriteria(factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
                Any(factKey) {
                override fun checkRule(): Boolean {
                    return checker(valueToCheck, facts.getIntFact(factKey).value)
                }
            }
        }
    }

    sealed class StringCriteria(factKey: String) : Criterion(factKey) {
        companion object {
            fun containsChecker(valueToCheck: String, value: String): Boolean {
                return value.contains(valueToCheck)
            }

            fun equalsChecker(valueToCheck: String, value: String): Boolean {
                return value == valueToCheck
            }
        }

        sealed class All(factKey: String) : StringCriteria(factKey) {
        }

        sealed class Any(factKey: String) : StringCriteria(factKey) {

        }

        sealed class Single(factKey: String) : StringCriteria(factKey) {

        }
    }
}


class FotW(private val onFactUpdated: (key: String) -> Unit = {}) {
    val facts = mutableMapOf<String, Factoid>()

    fun factsFor(vararg key: String): List<Factoid> {
        /*
        advanced queries, my main man

        To begin with, first scenario, we want all keys that contain
        this particular key, start to finish or something. Keys
        are a hierarchy
         */

        val fs = if (key.any { it.contains("*") } && key.count { it.contains("*") } == 1) {
            val mk = multiKey(*key)
            val start = mk.substringBefore("*")
            val end = mk.substringAfter("*")
            facts.filterKeys { it.startsWith(start) && it.endsWith(end) }.values.toList()
        } else {
            val mk = multiKey(*key)
            facts.filterKeys { it.contains(mk) }.values.toList()
        }
        return fs
    }

    fun setBooleanFact(value: Boolean, vararg key: String) {
        val mk = multiKey(*key)
        val fact = Factoid.Fact.BooleanFact(mk, value)
        if (!facts.containsKey(mk)) {
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.BooleanFact) {
            val fact = facts[mk]!! as Factoid.Fact.BooleanFact
            fact.value = value
        }
        //Check all this out later
        onFactUpdated(mk)
    }

    fun getBooleanFact(vararg key: String): Factoid.Fact.BooleanFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.BooleanFact)
                return facts[mk]!! as Factoid.Fact.BooleanFact
            else
                throw Exception("Fact $mk is not a Boolean")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun setIntFact(value: Int, vararg key: String) {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.IntFact(mk, value)
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.IntFact) {
            (facts[mk]!! as Factoid.Fact.IntFact).value = value
        }
        onFactUpdated(mk)
        throw Exception("Fact with key $mk is not of type Int")
    }

    fun getIntFact(vararg key: String): Factoid.Fact.IntFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.IntFact)
                return facts[mk]!! as Factoid.Fact.IntFact
            else
                throw Exception("Fact $mk is not a Int")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun setStringFact(value: String, vararg key: String) {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.StringFact(mk, value)
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.StringFact) {
            (facts[mk]!! as Factoid.Fact.StringFact).value = value
        }
        onFactUpdated(mk)
        throw Exception("Fact with key $mk is not of type String")
    }

    fun getStringFact(vararg key: String): Factoid.Fact.StringFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.StringFact)
                return facts[mk]!! as Factoid.Fact.StringFact
            else
                throw Exception("Fact $mk is not a String")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun addToStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.add(value)
        onFactUpdated(multiKey(*key))
    }

    fun removeFromStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.remove(value)
        onFactUpdated(multiKey(*key))
    }

    fun getStringList(vararg key: String): Factoid.Fact.StringListFact {
        return ensureStringList(*key)
    }

    fun ensureStringList(vararg key: String): Factoid.Fact.StringListFact {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val newFact = Factoid.Fact.StringListFact(mk, mutableListOf())
            facts[mk] = newFact
            return newFact
        } else if (facts[mk]!! is Factoid.Fact.StringListFact) {
            return facts[mk]!! as Factoid.Fact.StringListFact
        }
        throw Exception("Fact $mk exists and is not a stringlist")
    }

}