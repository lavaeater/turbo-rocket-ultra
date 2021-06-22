package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.SpriteDirection
import ui.BoundAnimationElement
import ui.BoundTextElement
import ui.CollectionContainerElement
import ui.TextureElement

class AnimEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }

    val commandManager = CommandManager()

    val fileName = "1.png"
    val baseFolder = "sheets"
    val region = TextureRegion(Texture(Gdx.files.internal("$baseFolder/$fileName")))

    private val altUi = TextureElement(region)

    override fun render(delta: Float) {
        super.render(delta)
        batch.use {
            altUi.render(batch, delta, debug)
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    /*

    Commands is a list of commands with names
    and functions they perform. They can be used to create menu systems
    with active commands and stuff like that.
     */


    override fun keyUp(keycode: Int): Boolean {
        return when(keycode) {
            else -> super.keyUp(keycode)
        }
    }


}

class Command(val name: String, action: () -> Unit) {

}

class CommandManager(val commandList: MutableList<Command> = mutableListOf()) : DataList<Command> {
    var activeItemIndex = 0
    override val activeItem get() = commandList[activeItemIndex]

    override fun previousItem() {
        activeItemIndex--
        activeItemIndex.coerceAtLeast(0)
    }

    override fun nextItem() {
        activeItemIndex++
        activeItemIndex.coerceAtMost(commandList.size - 1)
    }

    override val items get() = commandList
}

interface DataList<T> {
    val activeItem: T
    fun previousItem()
    fun nextItem()
    val items: List<T>
}
