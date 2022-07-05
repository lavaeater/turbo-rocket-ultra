package story.consequence

import eater.turbofacts.Factoid
import eater.turbofacts.TurboRule


interface Consequence {
  var rule: TurboRule
  var facts: Set<Factoid>
    val consequenceType: ConsequenceType
	fun apply()
}