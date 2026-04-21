package turbofacts

import dependencies.InjectionContext.Companion.inject

sealed class Criterion {
    abstract val factKey: String

    protected val facts by lazy { inject<TurboFactsOfTheWorld>() }
    abstract fun checkRule(): Boolean
    open fun toTextToken(): String? = null
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
            AnyStringOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::containsChecker, "anyStringContains")

        fun equals(factKey: String, valueToCheck: String) =
            AnyStringOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::equalsChecker, "anyStringEquals")
    }
    class AnyStringOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean, val token: String = ""): AnyString() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).any { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}

sealed class SingleString : StringCriteria() {
    companion object {
        fun contains(factKey: String, valueToCheck: String) =
            SingleStringOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::containsChecker, "stringContains")

        fun equals(factKey: String, valueToCheck: String) =
            SingleStringOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::equalsChecker, "stringEquals")
    }
    class SingleStringOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean, val token: String = ""): SingleString() {
        override fun checkRule(): Boolean {
            return checker(valueToCheck, facts.getStringFact(factKey).value)
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}

sealed class AllStrings : StringCriteria() {
    companion object {
        fun contain(factKey: String, valueToCheck: String): AllStringsOperatorCriteria {
            return AllStringsOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::containsChecker, "allStringContains")
        }

        fun equal(factKey: String, valueToCheck: String): AllStringsOperatorCriteria {
            return AllStringsOperatorCriteria(factKey, valueToCheck, StringCriteria.Companion::equalsChecker, "allStringEquals")
        }
    }
    class AllStringsOperatorCriteria(override val factKey: String, val valueToCheck:String, val checker: (String, String) -> Boolean, val token: String = ""): AllStrings() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).all { it is Factoid.Fact.StringFact && checker(valueToCheck, it.value) }
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}

sealed class BooleanCriteria : Criterion()
sealed class AllBooleans : BooleanCriteria() {
    class AreTrue(override val factKey: String) : AllBooleans() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.all { it is Factoid.Fact.BooleanFact && it.value }
        }
        override fun toTextToken() = "allBoolTrue $factKey"
    }

    class AreFalse(override val factKey: String) : AllBooleans() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.all { it is Factoid.Fact.BooleanFact && !it.value }
        }
        override fun toTextToken() = "allBoolFalse $factKey"
    }
}

sealed class AnyBoolean : BooleanCriteria() {
    class IsTrue(override val factKey: String) : AnyBoolean() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.any { it is Factoid.Fact.BooleanFact && it.value }
        }
        override fun toTextToken() = "anyBoolTrue $factKey"
    }

    class IsFalse(override val factKey: String) : AnyBoolean() {
        override fun checkRule(): Boolean {
            val facts = facts.factsFor(factKey)
            return facts.any { it is Factoid.Fact.BooleanFact && !it.value }
        }
        override fun toTextToken() = "anyBoolFalse $factKey"
    }
}


sealed class SingleBoolean : BooleanCriteria() {
    
    class IsTrue(override val factKey: String) : SingleBoolean() {
        override fun checkRule(): Boolean {
            return facts.getBooleanFact(factKey).value
        }
        override fun toTextToken() = "boolTrue $factKey"
    }


    class IsFalse(override val factKey: String) : SingleBoolean() {
        override fun checkRule(): Boolean {
            return !facts.getBooleanFact(factKey).value
        }
        override fun toTextToken() = "boolFalse $factKey"
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
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::lessThanChecker, "allIntLessThan")
        }

        fun moreThan(factKey: String, valueToCheck: Int): AllIntsOperatorCriteria {
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::moreThanChecker, "allIntMoreThan")
        }

        fun equals(factKey: String, valueToCheck: Int): AllIntsOperatorCriteria {
            return AllIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::equalsChecker, "allIntEquals")
        }
    }

    class AllIntsOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean, val token: String = "") :
        AllInts() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).all { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}


sealed class AnyInts : IntCriteria() {
    companion object {
        fun lessThan(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::lessThanChecker, "anyIntLessThan")
        }

        fun moreThan(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::moreThanChecker, "anyIntMoreThan")
        }

        fun equals(factKey: String, valueToCheck: Int): AnyIntsOperatorCriteria {
            return AnyIntsOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::equalsChecker, "anyIntEquals")
        }
    }

    class AnyIntsOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean, val token: String = "") :
        AnyInts() {
        override fun checkRule(): Boolean {
            return facts.factsFor(factKey).any { it is Factoid.Fact.IntFact && checker(valueToCheck, it.value) }
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}


sealed class SingleInt : IntCriteria() {
    companion object {
        fun lessThan(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::lessThanChecker, "intLessThan")
        }

        fun moreThan(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::moreThanChecker, "intMoreThan")
        }

        fun equals(factKey: String, valueToCheck: Int): SingleIntOperatorCriteria {
            return SingleIntOperatorCriteria(factKey, valueToCheck, IntCriteria.Companion::equalsChecker, "intEquals")
        }
    }

    class SingleIntOperatorCriteria(override val factKey: String, val valueToCheck: Int, val checker: (Int, Int) -> Boolean, val token: String = "") :
        AnyInts() {
        override fun checkRule(): Boolean {
            return checker(valueToCheck, facts.getIntFact(factKey).value)
        }
        override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $valueToCheck" else null
    }
}


sealed class IntVersusInt : IntCriteria() {
    companion object {
        fun lessThan(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria.Companion::lessThanChecker)
        }

        fun moreThan(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria.Companion::moreThanChecker)
        }

        fun equals(factToCheck: String, factToCheckAgainst: String): IntVersusIntOperatorCriteria {
            return IntVersusIntOperatorCriteria(factToCheck, factToCheckAgainst, IntCriteria.Companion::equalsChecker)
        }
    }
    
    class IntVersusIntOperatorCriteria(override val factKey: String, val factToCheckAgainst: String, val checker:(Int, Int) -> Boolean): IntVersusInt() {
        override fun checkRule(): Boolean {
            return checker(facts.getInt(factKey), facts.getInt(factToCheckAgainst))
        }
    }

}

sealed class StringListCriteria : Criterion()

class StringListContains(override val factKey: String, private val value: String) : StringListCriteria() {
    override fun checkRule(): Boolean = facts.getStringList(factKey).value.contains(value)
    override fun toTextToken() = "listContains $factKey $value"
}

class StringListSize(override val factKey: String, private val expected: Int, private val checker: (Int, Int) -> Boolean, private val token: String = "") : StringListCriteria() {
    override fun checkRule(): Boolean = checker(facts.getStringList(factKey).value.size, expected)
    override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $expected" else null

    companion object {
        fun moreThan(factKey: String, size: Int) = StringListSize(factKey, size, { a, b -> a > b }, "listSizeMoreThan")
        fun lessThan(factKey: String, size: Int) = StringListSize(factKey, size, { a, b -> a < b }, "listSizeLessThan")
        fun equals(factKey: String, size: Int)   = StringListSize(factKey, size, { a, b -> a == b }, "listSizeEquals")
    }
}

sealed class SetCriteria<V> : Criterion()

class SetContains<V>(override val factKey: String, private val value: V) : SetCriteria<V>() {
    override fun checkRule(): Boolean = facts.getSetFact<V>(factKey).value.contains(value)
    override fun toTextToken() = "setContains $factKey $value"
}

class SetSize<V>(override val factKey: String, private val expected: Int, private val checker: (Int, Int) -> Boolean, private val token: String = "") : SetCriteria<V>() {
    override fun checkRule(): Boolean = checker(facts.getSetFact<V>(factKey).value.size, expected)
    override fun toTextToken() = if (token.isNotEmpty()) "$token $factKey $expected" else null

    companion object {
        fun <V>moreThan(factKey: String, size: Int) = SetSize<V>(factKey, size, { a, b -> a > b }, "setSizeMoreThan")
        fun <V>lessThan(factKey: String, size: Int) = SetSize<V>(factKey, size, { a, b -> a < b }, "setSizeLessThan")
        fun <V>equals(factKey: String, size: Int)   = SetSize<V>(factKey, size, { a, b -> a == b }, "setSizeEquals")
    }
}
