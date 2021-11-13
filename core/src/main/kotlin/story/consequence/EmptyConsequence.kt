package story.consequence

import story.fact.IFact
import story.rule.Rule

class EmptyConsequence : Consequence {
  override fun apply() {
  }

  override lateinit var rule: Rule

  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.Empty
}