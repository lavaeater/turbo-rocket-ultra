package screens.charactereditor

import characterEditor.CharacterEditorView
import characterEditor.CharacterEditorViewModel
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import gamestate.GameEvent
import gamestate.GameState
import screens.basic.BasicScreen
import spritesheet.LpcSheetDefinitionLoader
import statemachine.StateMachine

class CharacterEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

	private val charEditor = CharacterEditorViewModel(LpcSheetDefinitionLoader.load())
	private val charEditorView = CharacterEditorView(batch, charEditor)

	override fun show() {
		charEditorView.show()
	}

	override fun render(delta: Float) {
		Gdx.gl.glClearColor(0.3f, 0.5f, 0.8f, 1f)
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		charEditorView.update(delta)
	}

	override fun resize(width: Int, height: Int) {
		charEditorView.resize(width, height) //viewPort.update(width, height)
		batch.projectionMatrix = camera.combined
	}
}

