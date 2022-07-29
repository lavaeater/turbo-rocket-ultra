package ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import story.conversation.IConversation

interface IUserInterface : Disposable {
    val stage: Stage
    val hudViewPort: Viewport
    fun show()
    fun hide()
    fun update(delta: Float)
    override fun dispose()
    fun clear()
    fun reset()
    fun pause()
    fun resume()
    fun runConversation(
        conversation: IConversation,
        function: () -> Unit,
        showProtagonistPortrait: Boolean,
        showAntagonistPortrait: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun worldToHudPosition(worldPosition: Vector2): Vector2


}