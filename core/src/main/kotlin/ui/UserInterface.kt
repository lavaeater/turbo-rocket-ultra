package ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.graphics.CharacterSpriteComponent
import gamestate.Players
import ktx.graphics.use
import ktx.math.vec2
import physics.drawScaled
import physics.getComponent
import tru.Assets

class UserInterface(
    private val batch: Batch,
    debug: Boolean = true
) : IUserInterface {

    private val players get() = Players.players
    private val camera = OrthographicCamera()
    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, camera)

    @ExperimentalStdlibApi
    override fun show() {
        hudViewPort.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    override fun hide() {
        //setup clears everything, not needed.
    }

    companion object {
        private const val aspectRatio = 16 / 9
        const val uiWidth = 800f
        const val uiHeight = uiWidth * aspectRatio
    }

    @ExperimentalStdlibApi
    override fun update(delta: Float) {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.use {
            newUi.render(batch)
        }
    }

    override fun dispose() {
    }

    override fun clear() {
    }

    @ExperimentalStdlibApi
    override fun reset() {
    }

    val enemyInfo = Queue<String>()

    @ExperimentalStdlibApi
    private val newUi by lazy {
        SimpleContainer(vec2(20f, hudViewPort.worldHeight / 6)).apply {
            for ((i, p) in players.values.withIndex()) {
                children.add(
                    TextActor(
                        "Player $i",
                        vec2(i * 10f, 10f)
                    )
                )
                children.add(
                    SpacedContainer(vec2(40f, 0f), vec2(i * 10f, 50f)).apply {
                        children.add(
                            TextureActor(
                                p.entity.getComponent<CharacterSpriteComponent>().currentAnim.keyFrames.first(),
                                vec2(0f, 0f),
                                0.5f
                            )
                        )
                        children.add(
                            TextureActor(
                                p.entity.getComponent<CharacterSpriteComponent>().currentAnim.keyFrames.first(),
                                vec2(0f, 0f),
                                0.5f
                            )
                        )
                        children.add(
                            TextureActor(
                                p.entity.getComponent<CharacterSpriteComponent>().currentAnim.keyFrames.first(),
                                vec2(0f, 0f),
                                0.5f
                            )
                        )
                    }
                )
            }
        }
    }
}

interface SimpleActor {
    fun render(batch: Batch, parentPosition: Vector2 = vec2())
}

abstract class ContainerBaseActor(val position: Vector2) : SimpleActor {
    val children = mutableListOf<SimpleActor>()
    override fun render(batch: Batch, parentPosition: Vector2) {
        for (child in children) {
            child.render(batch, position)
        }
    }
}

abstract class LeafActor(val position: Vector2) : SimpleActor {
    abstract override fun render(batch: Batch, parentPosition: Vector2)
}

class SimpleContainer(position: Vector2) : ContainerBaseActor(position)

class SpacedContainer(private val offset: Vector2, position: Vector2) : ContainerBaseActor(position) {
    override fun render(batch: Batch, parentPosition: Vector2) {
        for ((index, child) in children.withIndex()) {
            child.render(batch, vec2(parentPosition.x + index * offset.x, parentPosition.y -  index * offset.y))
        }
    }
}

class TextActor(
    var text: String,
    position: Vector2
) : LeafActor(position) {
    override fun render(batch: Batch, parentPosition: Vector2) {
        Assets.font.draw(
            batch,
            text,
            parentPosition.x + position.x,
            parentPosition.y - position.y
        )
    }

}

class TextureActor(
    val textureRegion: TextureRegion,
    position: Vector2,
    val scale: Float = 1f
) : LeafActor(position) {
    override fun render(batch: Batch, parentPosition: Vector2) {

        batch.drawScaled(
            textureRegion,
            parentPosition.x + position.x,
            parentPosition.y - position.y - textureRegion.regionHeight,
            scale,
            0f
        )
    }
}