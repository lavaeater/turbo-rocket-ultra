package ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport

interface IUserInterface : Disposable {
  val stage: Stage
  val hudViewPort: Viewport
  fun show()
  fun hide()
  fun update(delta: Float)
  override fun dispose()
  fun clear()
}