package story.conversation

import data.IAgent

interface IConversation {
  val antagonistCanSpeak:Boolean
  val protagonistCanChoose:Boolean
  val protagonist: IAgent
  val antagonist: IAgent
  val choiceCount: Int

  fun getAntagonistLines():Iterable<String>
  fun getProtagonistChoices():Iterable<String>
  fun makeChoice(index:Int) : Boolean
}