package story.consequence

import story.fact.IFact
import story.rule.Rule

class SimpleConsequence(private val applier:()->Unit): Consequence {
  override fun apply() {
    applier()
  }

  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
}