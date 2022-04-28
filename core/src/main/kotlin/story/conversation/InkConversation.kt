package story.conversation

import com.bladecoder.ink.runtime.Story
import data.IAgent
import factories.factsOfTheWorld

class InkConversation(val story:Story, override val protagonist: IAgent, override val antagonist: IAgent) : IConversation {
  private val factsOfTheWorld by lazy { factsOfTheWorld() }
  init {
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
  }
}

