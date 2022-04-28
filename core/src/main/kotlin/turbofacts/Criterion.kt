package turbofacts

import injection.Context
import java.util.zip.CheckedInputStream

sealed class Criterion(val factKey: String) {
    protected val facts by lazy { Context.inject<NewFactsOfTheWorld>() }
    abstract fun checkRule(): Boolean
    sealed class BooleanCriteria(factKey: String) : Criterion(factKey) {
        sealed class All(factKey: String) : BooleanCriteria(factKey) {
            class AreTrue(factKey: String) : All(factKey) {
                override fun checkRule(): Boolean {
                    val facts = facts.factsFor(factKey)
                    return facts.all { it is Factoid.Fact.BooleanFact && it.value }
                }
            }

            class AreFalse(factKey: String) : All(factKey) {
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

        sealed class Versus(val factToCheck: String, val factToCheckAgainst: String) : IntCriteria(factToCheck) {
            companion object {
                fun lessThan(factToCheck: String, factToCheckAgainst: String): VersusOperatorCriteria {
                    return VersusOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::lessThanChecker)
                }

                fun moreThan(factToCheck: String, factToCheckAgainst: String): VersusOperatorCriteria {
                    return VersusOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::moreThanChecker)
                }

                fun equals(factToCheck: String, factToCheckAgainst: String): VersusOperatorCriteria {
                    return VersusOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::equalsChecker)
                }
            }
            class VersusOperatorCriteria(factToCheck: String, factToCheckAgainst: String, val checker:(Int, Int) -> Boolean): Versus(factToCheck, factToCheckAgainst) {
                override fun checkRule(): Boolean {
                    return checker(facts.getInt(factToCheck), facts.getInt(factToCheckAgainst))
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
            companion object {
                fun contains(factKey: String, valueToCheck: String): AllOperatorCriteria {
                    return AllOperatorCriteria(factKey, valueToCheck, ::containsChecker)
                }

                fun equals(factKey: String, valueToCheck: String): AllOperatorCriteria {
                    return AllOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
                }
            }
            class AllOperatorCriteria(factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): All(factKey) {
                override fun checkRule(): Boolean {
                    return facts.factsFor(factKey).all { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
                }
            }
        }

        sealed class Any(factKey: String) : StringCriteria(factKey) {
            companion object {
                fun contains(factKey: String, valueToCheck: String) =
                    AnyOperatorCriteria(factKey, valueToCheck, ::containsChecker)

                fun equals(factKey: String, valueToCheck: String) =
                    AnyOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
            }
            class AnyOperatorCriteria(factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): Any(factKey) {
                override fun checkRule(): Boolean {
                    return facts.factsFor(factKey).any { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
                }
            }
        }

        sealed class Single(factKey: String) : StringCriteria(factKey) {
            companion object {
                fun contains(factKey: String, valueToCheck: String) =
                    SingleOperatorCriteria(factKey, valueToCheck, ::containsChecker)

                fun equals(factKey: String, valueToCheck: String) =
                    SingleOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
            }
            class SingleOperatorCriteria(factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): Single(factKey) {
                override fun checkRule(): Boolean {
                    return checker(valueToCheck, facts.getStringFact(factKey).value)
                }
            }
        }
    }
}