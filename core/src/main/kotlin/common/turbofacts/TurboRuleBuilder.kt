package common.turbofacts

class TurboRuleBuilder : Builder<TurboRule> {
    var name = "Rule Name"
    val criteria = mutableListOf<Criterion>()

    override fun build(): TurboRule = TurboRule(name, criteria)
}