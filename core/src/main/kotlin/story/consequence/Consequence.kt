package story.consequence

import common.turbofacts.Factoid
import common.turbofacts.TurboRule

interface Consequence {
  var rule: TurboRule
  var facts: Set<Factoid>
    val consequenceType: ConsequenceType
	fun apply()
}