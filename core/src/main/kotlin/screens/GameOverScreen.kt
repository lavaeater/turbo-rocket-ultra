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

class GameOverScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState) {

    private val skin get() = Scene2DSkin.defaultSkin
    private val facts by lazy { factsOfTheWorld() }
    private var rootTable: Table? = null

    override fun show() {
        super.show()
        // Rebuild each time so stats reflect the just-ended run
        rootTable?.remove()
        rootTable = scene2d.table {
            setFillParent(true)
            defaults().pad(10f).width(280f)

            add(Label("GAME OVER", skin, "title")).padBottom(28f).row()

            val level = facts.getInt(Factoids.CurrentLevel)
            val kills = facts.getInt(Factoids.EnemyKillCount)
            val mapName = facts.getString(Factoids.CurrentMapName)

            if (mapName.isNotBlank())
                add(Label(mapName.uppercase(), skin)).padBottom(4f).row()
            add(Label("Survived to level  $level", skin)).row()
            add(Label("Enemies killed:  $kills", skin)).padBottom(24f).row()

            val restartBtn = TextButton("Play Again", skin)
            restartBtn.onClick { gameState.acceptEvent(GameEvent.RestartGame) }
            add(restartBtn).row()

            val menuBtn = TextButton("Main Menu", skin)
            menuBtn.onClick { gameState.acceptEvent(GameEvent.ExitedGame) }
            add(menuBtn).row()
        }
        stage.addActor(rootTable)
    }

    override fun keyUp(keycode: Int): Boolean {
        return when (keycode) {
            Input.Keys.ENTER, Input.Keys.SPACE -> {
                gameState.acceptEvent(GameEvent.RestartGame); true
            }
            Input.Keys.ESCAPE -> {
                gameState.acceptEvent(GameEvent.ExitedGame); true
            }
            else -> super.keyUp(keycode)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage.viewport.update(width, height, true)
    }
}
