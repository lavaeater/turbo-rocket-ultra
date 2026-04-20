package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import gamestate.GameEvent
import gamestate.GameState
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table
import statemachine.StateMachine
import turbofacts.Factoids
import turbofacts.factsOfTheWorld

class PauseScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState) {

    private val skin get() = Scene2DSkin.defaultSkin
    private val facts by lazy { factsOfTheWorld() }
    private var rootTable: Table? = null

    override fun show() {
        super.show()
        if (rootTable == null) {
            rootTable = scene2d.table {
                setFillParent(true)
                defaults().pad(10f).width(240f)

                add(Label("PAUSED", skin, "title")).padBottom(28f).row()

                val mapName = facts.getString(Factoids.CurrentMapName)
                if (mapName.isNotBlank())
                    add(Label(mapName.uppercase(), skin)).padBottom(4f).row()
                add(Label("Level  ${facts.getInt(Factoids.CurrentLevel)}", skin)).padBottom(20f).row()

                val resumeBtn = TextButton("Resume  [P]", skin)
                resumeBtn.onClick { gameState.acceptEvent(GameEvent.ResumedGame) }
                add(resumeBtn).row()

                val quitBtn = TextButton("Quit to Menu", skin)
                quitBtn.onClick { gameState.acceptEvent(GameEvent.ExitedGame) }
                add(quitBtn).row()
            }
            stage.addActor(rootTable)
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        return when (keycode) {
            Input.Keys.ESCAPE, Input.Keys.P -> {
                gameState.acceptEvent(GameEvent.ResumedGame); true
            }
            else -> super.keyUp(keycode)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage.viewport.update(width, height, true)
    }
}
