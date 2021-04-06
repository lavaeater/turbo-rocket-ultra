package screens

import com.badlogic.gdx.Gdx
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import statemachine.StateMachine
import tru.Assets
import ui.TextureActor
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

    val debug = true
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val ui = rootSpacedContainer {
        position = vec2(100f, 100f)
        offset = vec2(20f, 20f)
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
        textLabel("Test Text")
    }

    override fun render(delta: Float) {
        super.render(delta)
        batch.use {
            ui.render(batch, debug = true)
            if(debug) {
                shapeDrawer.filledCircle(vec2(), 5f)
            }
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(true)
        viewPort.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }

}
