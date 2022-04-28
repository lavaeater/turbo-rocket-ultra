package story.conversation

import data.IAgent
import factories.factsOfTheWorld

class InlineConvo(override val protagonist: IAgent, override val antagonist: IAgent = EmptyAgent(), val antagonistLines: Map<Int, List<String>> = mapOf()) : IConversation {

  private var storyIndex = 0
  val factsOfTheWorld by lazy { factsOfTheWorld()}

  override val antagonistCanSpeak: Boolean
    get() = storyIndex >= 0 && storyIndex < antagonistLines.keys.count()
  override val protagonistCanChoose: Boolean
    get() = !quit
  override val choiceCount: Int
    get() = 4

  override fun getAntagonistLines(): Iterable<String> {
    if(storyIndex < 0)
      quit = true
    return if(!quit) getForIndex(storyIndex) else emptyList()
  }

  private fun getForIndex(index: Int): Iterable<String> {
    return antagonistLines[index]!!
  }

  override fun getProtagonistChoices(): Iterable<String> {
    return setOf("Ja", "Nej", "Driver du med mig?", "Hejdå")
  }

  private var quit: Boolean = false

  override fun makeChoice(index: Int): Boolean {
    when(index) {
      0 -> storyIndex++//Ja
      1 -> storyIndex--//Nej
      2,3 -> quit = true
    }
    return true
  }

}


