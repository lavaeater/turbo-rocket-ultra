package turbofacts

import ecs.systems.enemy.multiKey

class TurboRule(val name: String, val criteria: List<Criterion>) {
    fun checkRule(): Boolean {
        return criteria.all { it.checkRule() }
    }
}

fun TurboRuleBuilder.allTrue(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.All.AreTrue(multiKey(*key)))
}
fun TurboRuleBuilder.allFalse(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.All.AreFalse(multiKey(*key)))
}

fun TurboRuleBuilder.anyTrue(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.Any.IsTrue(multiKey(*key)))
}
fun TurboRuleBuilder.anyFalse(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.Any.IsFalse(multiKey(*key)))
}

fun TurboRuleBuilder.isTrue(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.Single.IsTrue(multiKey(*key)))
}
fun TurboRuleBuilder.isFalse(vararg key: String) {
    criteria.add(Criterion.BooleanCriteria.Single.IsFalse(multiKey(*key)))
}

fun TurboRuleBuilder.allStringEquals(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.All.equals(value, multiKey(*key)))
}

fun TurboRuleBuilder.allStringContains(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.All.contains(value, multiKey(*key)))
}

fun TurboRuleBuilder.anyStringEquals(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.Any.equals(value, multiKey(*key)))
}

fun TurboRuleBuilder.anyStringContains(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.Any.contains(value, multiKey(*key)))
}

fun TurboRuleBuilder.stringEquals(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.Single.equals(value, multiKey(*key)))
}

fun TurboRuleBuilder.stringContains(value: String, vararg key: String) {
    criteria.add(Criterion.StringCriteria.Single.contains(value, multiKey(*key)))
}


fun TurboRuleBuilder.allIntEquals(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.All.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.allIntMoreThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.All.moreThan(multiKey(*key), value))
}
fun TurboRuleBuilder.allIntLessThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.All.lessThan(multiKey(*key), value))
}

fun TurboRuleBuilder.anyIntEquals(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Any.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.anyIntMoreThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Any.moreThan(multiKey(*key), value))
}
fun TurboRuleBuilder.anyIntLessThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Any.lessThan(multiKey(*key), value))
}

fun TurboRuleBuilder.intEquals(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Single.equals(multiKey(*key), value))
}
fun TurboRuleBuilder.intMoreThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Single.moreThan(multiKey(*key), value))
}

fun TurboRuleBuilder.intMoreThan(factToCheck: String, factToCheckAgainst: String) {
    criteria.add(Criterion.IntCriteria.Versus.moreThan(factToCheck, factToCheckAgainst))
}

fun TurboRuleBuilder.intLessThan(value: Int, vararg key: String) {
    criteria.add(Criterion.IntCriteria.Single.lessThan(multiKey(*key), value))
}

