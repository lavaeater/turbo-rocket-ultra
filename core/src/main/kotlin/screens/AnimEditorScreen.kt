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
import ui.*

class AnimEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }

    val commandManager = CommandManager()

    private val fileName = "2.png"
    private val baseFolder = "sheets"
    private val texture = Texture(Gdx.files.internal("$baseFolder/$fileName"))
    private val region = TextureRegion(texture)


    private val textureElement = TextureElement(region)
    private val boundGridElement = BoundGridElement(
        region.regionWidth.toFloat(),
        region.regionHeight.toFloat(),
        gridUpdated = textureElement::gridUpdated
    )
    private val animEditorElement = AnimationEditorElement(texture, boundGridElement::gridWidth, boundGridElement::gridHeight)

    private val inputters = listOf(
        Inputter(
            "Grid",
            mapOf(
                Input.Keys.LEFT to boundGridElement::decrementGridWidth,
                Input.Keys.RIGHT to boundGridElement::incrementGridWidth,
                Input.Keys.UP to boundGridElement::incrementGridHeight,
                Input.Keys.DOWN to boundGridElement::decrementGridHeight
            )
        ))
    private var inputIndex = 0
    private val currentInputter get() = inputters[inputIndex]

    private val altUi = ContainerElement(vec2()).apply {
        addChild(textureElement)
        addChild(boundGridElement)
        addChild(animEditorElement)
        addChild(BindableTextElement({ "width: ${boundGridElement.gridWidth}" }, vec2(200f, -200f)))
        addChild(BindableTextElement({ "height: ${boundGridElement.gridHeight}" }, vec2(200f, -220f)))
        addChild(BindableTextElement({currentInputter.name}, vec2(300f, -300f)))
    }

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

    override fun keyUp(keycode: Int): Boolean {
        return currentInputter.handleInput(keycode)
    }

    private fun decrement(gridWidth: Float): Boolean {
        TODO("Not yet implemented")
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

class Inputter(val name: String, val inputMap: Map<Int, () -> Unit>) {
    fun handleInput(keyCode: Int): Boolean {
        if (inputMap.containsKey(keyCode)) {
            inputMap[keyCode]!!()
        }
        return true
    }
}

interface DataList<T> {
    val activeItem: T
    fun previousItem()
    fun nextItem()
    val items: List<T>
}
