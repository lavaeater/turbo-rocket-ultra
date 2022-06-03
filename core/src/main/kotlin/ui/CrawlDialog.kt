package ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import gamestate.GameEvent
import gamestate.GameState
import injection.Context
import ktx.actors.centerPosition
import ktx.actors.then
import ktx.scene2d.label
import ktx.scene2d.scene2d
import statemachine.StateMachine

object CrawlDialog {
    private val gameState by lazy { Context.inject<StateMachine<GameState, GameEvent>>() }
    fun getLabelsFromString(text: String, dialogHeight: Float, duration: Float, autoQuit: Boolean): MutableList<Label> {
        val lines = text.lines()
        return lines.mapIndexed { index, line ->
            scene2d.label(line) {
                addAction(
                    Actions.moveBy(0f, dialogHeight, duration, Interpolation.linear).then(Actions.removeActor())
                )
            }
        }.toMutableList().apply {
            add(scene2d.label("Press Any Key") {
                if (autoQuit) {
                    addAction(
                        Actions.moveBy(0f, dialogHeight, duration, Interpolation.linear).then(
                            object : Action() {
                                override fun act(delta: Float): Boolean {
                                    if (gameState.currentState.state == GameState.Paused)
                                        gameState.acceptEvent(GameEvent.ResumedGame)
                                    return true
                                }
                            }).then(Actions.removeActor())
                    )
                } else
                    addAction(
                        Actions.moveBy(0f, dialogHeight, duration, Interpolation.linear).then(Actions.removeActor())
                    )
            })
        }
    }

    fun showDialog(
        dialog: Dialog,
        text: String,
        dialogHeight: Float = 200f,
        duration: Float = 10f,
        autoQuit: Boolean = true
    ) {
        val linesToScroll = getLabelsFromString(text, dialogHeight, duration, autoQuit)
        val stage = dialog.stage
        val textHeight = linesToScroll.maxOf { it.height }
        val textWidth = linesToScroll.maxOf { it.width } * 1.5f

        dialog.show(stage, Actions.repeat(linesToScroll.size, Actions.delay(1f, object : Action() {
            override fun act(delta: Float): Boolean {
                val l = linesToScroll.removeFirst()
                stage.addActor(l)
                l.setPosition(
                    stage.width / 2 - (l.width / 2f),
                    stage.height / 2 - dialog.height / 2 + textHeight
                )
                return true
            }
        })))
        dialog.apply {
            contentTable.setFillParent(true)
            width = textWidth
            height = dialogHeight * 1.25f
            //delay can be calculated from label heights.
            centerPosition()
        }
    }
}