package screens.charactereditor

import charactereditor.CharacterEditorView
import charactereditor.CharacterEditorViewModel
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import gamestate.GameEvent
import gamestate.GameState
import screens.basic.BasicScreen
import spritesheet.LpcSpriteSheetHelper
import spritesheet.SheetDef
import spritesheet.TextureRegionDef
import statemachine.StateMachine

class CharacterEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

	private val basePath = "localfiles/lpc"
	private val charEditor = CharacterEditorViewModel(LpcSpriteSheetHelper().categories)
	private val charEditorView = CharacterEditorView(batch, charEditor)

	private var currentAnim = 2

	private val sheetDef = SheetDef(
			"JustWalkin'",
			listOf(
					TextureRegionDef("walknorth", 8, 9),
					TextureRegionDef("walkwest", 9, 9),
					TextureRegionDef("walksouth", 10, 9),
					TextureRegionDef("walkeast", 11, 9)
			))

	private val genders = listOf("male", "female")

	override fun show() {
//		camera.position.set(125f, 125f, 0f)
//		camera.update()
		charEditorView.show()
	}

	private val fps = 1f / 9f
	private var accDelta = 0f
	private var currentFrame = 1

	override fun render(delta: Float) {
		Gdx.gl.glClearColor(0.3f, 0.5f, 0.8f, 1f)
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		accDelta += delta

		if(accDelta > fps) {
			accDelta = 0f
			currentFrame++

			if(currentFrame > 8)
				currentFrame = 1
		}
		charEditorView.update(delta)
	}

	override fun resize(width: Int, height: Int) {
		charEditorView.resize(width, height) //viewPort.update(width, height)
		batch.projectionMatrix = camera.combined
	}
}

