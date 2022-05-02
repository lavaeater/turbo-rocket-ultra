package turbofacts

import ecs.systems.enemy.multiKey

class TurboRule(val name: String, val criteria: List<Criterion>) {
    fun checkRule(): Boolean {
        return criteria.all { it.checkRule() }
    }
}

fun TurboRuleBuilder.allTrue(vararg key: String) {
    criteria.add(AllBooleans.AreTrue(multiKey(*key)))
}
fun TurboRuleBuilder.allFalse(vararg key: String) {
    criteria.add(AllBooleans.AreFalse(multiKey(*key)))
}

fun TurboRuleBuilder.anyTrue(vararg key: String) {
    criteria.add(AnyBoolean.IsTrue(multiKey(*key)))
}
fun TurboRuleBuilder.anyFalse(vararg key: String) {
    criteria.add(AnyBoolean.IsFalse(multiKey(*key)))
}

fun TurboRuleBuilder.isTrue(vararg key: String) {
    criteria.add(SingleBoolean.IsTrue(multiKey(*key)))
}
fun TurboRuleBuilder.isFalse(vararg key: String) {
    criteria.add(SingleBoolean.IsFalse(multiKey(*key)))
}

fun TurboRuleBuilder.allStringEquals(value: String, vararg key: String) {
    criteria.add(AllStrings.equal(value, multiKey(*key)))
}

fun TurboRuleBuilder.allStringContains(value: String, vararg key: String) {
    criteria.add(AllStrings.contain(value, multiKey(*key)))
}

fun TurboRuleBuilder.anyStringEquals(value: String, vararg key: String) {
    criteria.add(AnyString.equals(value, multiKey(*key)))
}

fun TurboRuleBuilder.anyStringContains(value: String, vararg key: String) {
    criteria.add(AnyString.contains(value, multiKey(*key)))
}

fun TurboRuleBuilder.stringEquals(value: String, vararg key: String) {
    criteria.add(SingleString.equals(value, multiKey(*key)))
}

fun TurboRuleBuilder.stringContains(value: String, vararg key: String) {
    criteria.add(SingleString.contains(value, multiKey(*key)))
}


fun TurboRuleBuilder.allIntEquals(value: Int, vararg key: String) {
    criteria.add(AllInts.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.allIntMoreThan(value: Int, vararg key: String) {
    criteria.add(AllInts.moreThan(multiKey(*key), value))
}
fun TurboRuleBuilder.allIntLessThan(value: Int, vararg key: String) {
    criteria.add(AllInts.lessThan(multiKey(*key), value))
}

fun TurboRuleBuilder.anyIntEquals(value: Int, vararg key: String) {
    criteria.add(AnyInts.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.anyIntMoreThan(value: Int, vararg key: String) {
    criteria.add(AnyInts.moreThan(multiKey(*key), value))
}
fun TurboRuleBuilder.anyIntLessThan(value: Int, vararg key: String) {
    criteria.add(AnyInts.lessThan(multiKey(*key), value))
}

fun TurboRuleBuilder.intEquals(value: Int, vararg key: String) {
    criteria.add(SingleInt.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.intMoreThan(value: Int, vararg key: String) {
    criteria.add(SingleInt.moreThan(multiKey(*key), value))
}

fun TurboRuleBuilder.intMoreThan(factToCheck: String, factToCheckAgainst: String) {
    criteria.add(IntVersusInt.moreThan(factToCheck, factToCheckAgainst))
}

fun TurboRuleBuilder.intLessThan(value: Int, vararg key: String) {
    criteria.add(SingleInt.lessThan(multiKey(*key), value))
}

