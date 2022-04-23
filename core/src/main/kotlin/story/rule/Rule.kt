package story.rule

import story.consequence.Consequence
import story.consequence.EmptyConsequence
import story.fact.IFact

class Rule(var name: String = "",
           val criteria: MutableCollection<Criterion> = mutableListOf(),
           var consequence: Consequence = EmptyConsequence()) {

  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<IFact<*>> = mutableSetOf()

	fun pass(facts: Set<IFact<*>>) : Boolean {

		if(facts.count() >= criteriaCount) {
			/*
			If we have more facts than criteria, that MIGHT mean that we have enough facts to
			say "this works right now"

			Maybe I am muddling the picture, I am trying to play
			sounds for different enemies in the game, but perhaps this is
			not what the rule system is mean to be used for.

			Perhaps we should avoid using FUZZY keys, and instead
			use some kind of compound keys. I know, we should use a KEY
			FUNCTION for the rule, instead. That means we could generate the
			specific keys for a certain component or anything at all in the game

			Like multiple keys. A rule for all enemies, for instance. So that criteria
			would be take one or two functions - one to generate a list to check and
			one to generate every key. That would be better!
			 */



			val res = facts.all {
				// THis is where we need to remove facts that do not in fact belong here.

				f -> criteria.first { if(it.fuzzyKey) f.key.contains(it.key) else it.key == f.key }.isMatch(f) }
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

