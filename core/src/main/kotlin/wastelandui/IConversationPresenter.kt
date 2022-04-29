package wastelandui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import story.conversation.IConversation

interface IConversationPresenter :Disposable{
  val s: Stage
  val conversation: IConversation
  val conversationEnded: () -> Unit
  override fun dispose()
}