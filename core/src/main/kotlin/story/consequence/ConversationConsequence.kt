package story.consequence

import com.bladecoder.ink.runtime.Story
import factories.factsOfTheWorld
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import statemachine.StateMachine
import story.conversation.ConversationManager
import eater.turbofacts.Factoid
import eater.turbofacts.TurboRule

class ConversationConsequence (
    val story: Story,
    private var afterConversation: (story: Story) -> Unit = {},
    private var beforeConversation: (story: Story)-> Unit = {}
    ): Consequence {

  private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }
  private val conversationManager by lazy { inject<ConversationManager>()}
  private val factsOfTheWorld by lazy { factsOfTheWorld() }

  override fun apply() {

//    val npc = factsOfTheWorld.getCurrentNpc()
//    if (npc != null) {//If null something is weird
//      gameState.handleEvent(GameEvents.DialogStarted)
//
//      beforeConversation(story)
//
//      conversationManager.startConversation(InkConversation(story, player, npc), {
//        afterConversation(story)
//        story.resetState()
//      })
//    }
  }
  /*

  This is an experiment.

  We will try to encapsulate everything that is needed to manage a conversation using ink
  into *this* class. We will lazily load a conversation manager and run the ui ON it... fucking
  great.

  For this particular thingie we will have all the facts that we know about the world...
  I would like to be able to load more facts, not hardcode them, but we will see what we
  need. First some testing of my actual job, though.
   */

  override lateinit var rule: TurboRule
  override lateinit var facts: Set<Factoid>
    override val consequenceType = ConsequenceType.ConversationLoader
}
