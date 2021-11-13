package story.consequence

import story.fact.IFact
import story.rule.Rule

interface Consequence {
  var rule: Rule
  var facts: Set<IFact<*>>
	val consequenceType: ConsequenceType
	fun apply()
}