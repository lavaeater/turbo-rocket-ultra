package story.conversation

import data.EmptyAgent
import data.IAgent
import data.Player
import injection.Ctx

class InternalConversation(private val startingStepKey:String, private val conversationSteps: Map<String, ConversationStep>, override val antagonist: IAgent = EmptyAgent()) : IConversation {

  var currentStep: ConversationStep = conversationSteps[startingStepKey]!!

  override val protagonist: IAgent by lazy { Ctx.context.inject<Player>() }
  override val antagonistCanSpeak: Boolean
    get() = currentStep.antagonistLines.any()
  override val protagonistCanChoose: Boolean
    get() = currentStep.conversationRoutes.any()
  override val choiceCount: Int
    get() = currentStep.conversationRoutes.count()

  override fun getAntagonistLines(): Iterable<String> {
    return currentStep.antagonistLines
  }

  override fun getProtagonistChoices(): Iterable<String> {
    return currentStep.conversationRoutes.map { it.text }
  }

  override fun makeChoice(index: Int): Boolean {
    if(index >= 0 && index < currentStep.conversationRoutes.count()) {
      //try to set a new step?
      val newStep = conversationSteps[currentStep.conversationRoutes.map { it.key }.toTypedArray()[index]]
      if(newStep != null) {
        currentStep = newStep
        return true
      }
    }
    return false
  }
}