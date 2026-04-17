package story.consequence

import common.turbofacts.Factoid
import common.turbofacts.TurboRule

class SimpleConsequence(private val applier:()->Unit): Consequence {
  override fun apply() {
    applier()
  }

  override lateinit var rule: TurboRule
  override lateinit var facts: Set<Factoid>
    override val consequenceType = ConsequenceType.ApplyLambdaConsequence
}