package story.consequence

import eater.turbofacts.Factoid
import eater.turbofacts.TurboRule

class SimpleConsequence(private val applier:()->Unit): Consequence {
  override fun apply() {
    applier()
  }

  override lateinit var rule: TurboRule
  override lateinit var facts: Set<Factoid>
    override val consequenceType = ConsequenceType.ApplyLambdaConsequence
}