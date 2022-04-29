package story.rule

import story.consequence.Consequence
import story.consequence.EmptyConsequence
import story.fact.IFact

class Rule(var name: String = "",
           private val criteria: MutableCollection<Criterion> = mutableListOf(),
           var consequence: Consequence = EmptyConsequence()) {

  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<IFact<*>> = mutableSetOf()

	fun pass(facts: Set<IFact<*>>) : Boolean {

		if(facts.count() >= criteriaCount) {
			val res = facts.all {
				f -> criteria.first { it.key == f.key }.isMatch(f) }
			if (res) {
				matchedFacts = facts
				consequence.facts = matchedFacts
				consequence.rule = this
				return true
			}
		}
		return false
	}
}

