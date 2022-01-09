package ui

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import story.conversation.IConversation

interface IUserInterface : Disposable {
  val hudViewPort: Viewport
  fun show()
  fun hide()
  fun update(delta: Float)
  override fun dispose()
  fun clear()
  fun reset()
  fun runConversation(
    conversation: IConversation,
    function: () -> Unit,
    showProtagonistPortrait: Boolean,
    showAntagonistPortrait: Boolean
  ) {
    TODO("Not yet implemented")
  }

}