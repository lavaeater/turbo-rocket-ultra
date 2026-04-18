package story.conversation

import com.bladecoder.ink.runtime.Story
import data.IAgent
import turbofacts.TurboFactsOfTheWorld
import turbofacts.factsOfTheWorld

class InkConversation(val story: Story, override val protagonist: IAgent, override val antagonist: IAgent) : IConversation {
    private val factsOfTheWorld by lazy { factsOfTheWorld() }

    /**
     * Reads Ink variable values and mirrors them into TurboFactsOfTheWorld.
     * Keys in [varToFactKey] map Ink variable names to fact keys (dot-joined).
     * Call this in the afterConversation callback to persist narrative state.
     */
    fun syncToFacts(facts: TurboFactsOfTheWorld = factsOfTheWorld, varToFactKey: Map<String, String> = defaultVarMapping) {
        for ((inkVar, factKey) in varToFactKey) {
            try {
                when (val v = story.variablesState[inkVar]) {
                    is Boolean -> facts.setBooleanFact(v, factKey)
                    is Int -> facts.setIntFact(v, factKey)
                    is Float -> facts.setFloatFact(v, factKey)
                    is String -> if (v.isNotEmpty()) facts.setStringFact(v, factKey)
                    else -> {}
                }
            } catch (_: Exception) {
                // Variable doesn't exist in this story — skip silently
            }
        }
    }
  override val antagonistCanSpeak: Boolean
    get() = story.canContinue()
  override val protagonistCanChoose: Boolean
    get() = story.currentChoices.size > 0

  override val choiceCount: Int
    get() = story.currentChoices.size

  override fun getAntagonistLines(): Iterable<String> {
    val lines = mutableListOf<String>()
    if(story.canContinue()) {
      while (story.canContinue()) {
        lines.add(story.Continue())
      }
    }
    return lines
  }

  override fun getProtagonistChoices(): Iterable<String> {
      return story.currentChoices.map { it.text }
  }

  override fun makeChoice(index: Int): Boolean {
    if(index in 0 until story.currentChoices.size) {
      story.chooseChoiceIndex(index)
      return true
    }
    return false
  }

  companion object {
    val MET_BEFORE = "met_before"
    val PLAYER_NAME = "player_name"
    val REACTION_SCORE = "reaction_score"
    val STEP_OF_STORY = "step_of_story"

    val defaultVarMapping = mapOf(
        MET_BEFORE to "conversation.met_before",
        PLAYER_NAME to "conversation.player_name",
        REACTION_SCORE to "conversation.reaction_score",
        STEP_OF_STORY to "conversation.step_of_story"
    )
  }
}

