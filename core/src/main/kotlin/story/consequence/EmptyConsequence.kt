package story.consequence

import turbofacts.Factoid
import turbofacts.TurboRule

class EmptyConsequence : Consequence {
  override fun apply() {
  }

  override lateinit var rule: TurboRule

  override lateinit var facts: Set<Factoid>
    override val consequenceType = ConsequenceType.Empty
}