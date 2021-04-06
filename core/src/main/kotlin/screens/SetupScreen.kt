package screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import statemachine.StateMachine
import tru.Assets
import ui.ActualActor
import ui.builders.rootSpacedContainer
import ui.builders.textLabel

class SetupScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    /*
    Setup screen should be the classic "press x / space to join
    When doing so, you get to see the actual character you are playing and

    SHOULD be able to customize it.

    That would be fudging fabulous.

    Customizing would require that every character was like 300 sprites or something, but it could totally be worth
    it.
     */
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    val debug = true
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val ui = rootSpacedContainer {
        position = vec2(100f, 500f)
        offset = vec2(20f, 20f)
        textLabel("Press buttons and shit to start the game, shitface!")
    }

    val altUi = ActualActor()

    override fun render(delta: Float) {
        super.render(delta)
        batch.use {
            ui.render(batch, debug = true)
            altUi.render(batch, debug)
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }

}
