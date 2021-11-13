package story.conversation

import data.IAgent
import story.rule.Rule

class RuleBasedConversation : IConversation {
  val rules = mutableSetOf<Rule>()
  override val protagonist: IAgent get()= TODO()
  override val antagonist: IAgent get()= TODO()
  override val antagonistCanSpeak: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val protagonistCanChoose: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val choiceCount: Int
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

  override fun getAntagonistLines(): Iterable<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getProtagonistChoices(): Iterable<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun makeChoice(index: Int): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}