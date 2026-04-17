package story.consequence

import common.turbofacts.Factoid
import common.turbofacts.TurboRule


class EmptyConsequence : Consequence {
  override fun apply() {
  }

  override lateinit var rule: TurboRule

  override lateinit var facts: Set<Factoid>
    override val consequenceType = ConsequenceType.Empty
}