package story

import story.rule.Rule

class RulesOfTheWorld {
	private val rulesOfTheWorld = mutableMapOf<String, Rule>()

	fun addRule(rule: Rule) {
		rulesOfTheWorld[rule.name] = rule
	}

	fun removeRuleByName(name:String) {
		rulesOfTheWorld.remove(name)
	}

	fun findRuleByName(name:String) : Rule? {
		return rulesOfTheWorld[name]
	}

	val rules : Set<Rule> get() { return rulesOfTheWorld.values.toSet() }
}