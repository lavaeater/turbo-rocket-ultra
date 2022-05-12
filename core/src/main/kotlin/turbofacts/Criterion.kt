package turbofacts

import injection.Context

sealed class Criterion {
    abstract val factKey: String

    protected val facts by lazy { Context.inject<TurboFactsOfTheWorld>() }
    abstract fun checkRule(): Boolean
}
sealed class StringCriteria : Criterion() {
    companion object {
        fun containsChecker(valueToCheck: String, value: String): Boolean {
            return value.contains(valueToCheck)
        }

        fun equalsChecker(valueToCheck: String, value: String): Boolean {
            return value == valueToCheck
        }
    }
}
sealed class AnyString: StringCriteria() {
    companion object {
        fun contains(factKey: String, valueToCheck: String) =
            AnyStringOperatorCriteria(factKey, valueToCheck, ::containsChecker)

        fun equals(factKey: String, valueToCheck: String) =
            AnyStringOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
    }
    class AnyStringOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): AnyString() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).any { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
        }
    }
}

sealed class SingleString : StringCriteria() {
    companion object {
        fun contains(factKey: String, valueToCheck: String) =
            SingleStringOperatorCriteria(factKey, valueToCheck, ::containsChecker)

        fun equals(factKey: String, valueToCheck: String) =
            SingleStringOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
    }
    class SingleStringOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): SingleString() {
        override fun checkRule(): Boolean {
            return checker(valueToCheck, facts.getStringFact(factKey).value)
        }
    }
}

sealed class AllStrings : StringCriteria() {
    companion object {
        fun contain(factKey: String, valueToCheck: String): AllStringsOperatorCriteria {
            return AllStringsOperatorCriteria(factKey, valueToCheck, ::containsChecker)
        }

        fun equal(factKey: String, valueToCheck: String): AllStringsOperatorCriteria {
            return AllStringsOperatorCriteria(factKey, valueToCheck, ::equalsChecker)
        }
    }
    class AllStringsOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean): AllStrings() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).all { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
        }
    }
}

sealed class BooleanCriteria : Criterion()
sealed class AllBooleans : BooleanCriteria() {
    class AreTrue(override val factKey: String) : AllBooleans() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.all { it is Factoid.Fact.BooleanFact && it.value }
        }
    }

    class AreFalse(override val factKey: String) : AllBooleans() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.all { it is Factoid.Fact.BooleanFact && !it.value }
        }
    }
}

sealed class AnyBoolean : BooleanCriteria() {
    class IsTrue(override val factKey: String) : AnyBoolean() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.any { it is Factoid.Fact.BooleanFact && it.value }
        }
    }

    class IsFalse(override val factKey: String) : AnyBoolean() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.any { it is Factoid.Fact.BooleanFact && !it.value }
        }
    }
}


sealed class SingleBoolean : BooleanCriteria() {
    
    class IsTrue(override val factKey: String) : SingleBoolean() {
        override fun checkRule(): Boolean {
            return facts.getBooleanFact(factKey).value
        }
    }

    
    class IsFalse(override val factKey: String) : SingleBoolean() {
        override fun checkRule(): Boolean {
            return !facts.getBooleanFact(factKey).value
        }
    }
}


sealed class IntCriteria : Criterion() {
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
}


sealed class AllInts : IntCriteria() {
    companion object {
        fun lessThan(factKey: String, valueToCheck: Int): AllIntsOperatorCriteria {
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
        }

        fun moreThan(factKey: String, valueToCheck: Int): AllIntsOperatorCriteria {
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
        }

        fun equals(factKey: String, valueToCheck: Int): AllIntsOperatorCriteria {
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
        }
    }

    
    class AllIntsOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
        AllInts() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).all { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
        }
    }
}


sealed class AnyInts : IntCriteria() {
    companion object {
        fun lessThan(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
        }

        fun moreThan(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
        }

        fun equals(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
        }
    }

    
    class AnyIntsOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
        AnyInts() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).any { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
        }
    }
}


sealed class SingleInt : IntCriteria() {
    companion object {
        fun lessThan(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria::lessThanChecker)
        }

        fun moreThan(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria::moreThanChecker)
        }

        fun equals(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria::equalsChecker)
        }
    }

    
    class SingleIntOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean) :
        AnyInts() {
        override fun checkRule(): Boolean {
            return checker(valueToCheck, facts.getIntFact(factKey).value)
        }
    }
}


sealed class IntVersusInt : IntCriteria() {
    companion object {
        fun lessThan(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::lessThanChecker)
        }

        fun moreThan(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::moreThanChecker)
        }

        fun equals(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria::equalsChecker)
        }
    }
    
    class IntVersusIntOperatorCriteria(override val factKey: String, val factToCheckAgainst: String, val checker:(Int, Int) -> Boolean): IntVersusInt() {
        override fun checkRule(): Boolean {
            return checker(facts.getInt(factKey), facts.getInt(factToCheckAgainst))
        }
    }

}
