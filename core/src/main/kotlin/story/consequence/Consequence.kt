package story.consequence

import turbofacts.Factoid
import turbofacts.TurboRule


interface Consequence {
  var rule: TurboRule
  var facts: Set<Factoid>
    val consequenceType: ConsequenceType
	fun apply()
}