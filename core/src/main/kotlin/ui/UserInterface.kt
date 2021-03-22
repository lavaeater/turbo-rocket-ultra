package ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.*
import factories.player
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import physics.to360Degrees
import physics.toDegrees

class UserInterface(
    private val batch: Batch,
    debug: Boolean = false
) : IUserInterface {

    private val controlMapper: ControlMapper by lazy { inject() }
    private val engine: Engine by lazy { inject() }
    private val playerEntity: Entity by lazy { engine.getEntitiesFor(allOf(PlayerComponent::class).get()).first() }
    private val transform: TransformComponent by lazy { playerEntity[mapperFor()]!! }
    private val bodyComponent: BodyComponent by lazy { playerEntity[mapperFor()]!! }
    private val playerControlComponent: PlayerControlComponent by lazy { playerEntity[mapperFor()]!! }
    private val bodyPos get() = bodyComponent.body.position
    private val bodyRotation get() = bodyComponent.body.angle

    private val firing get() = playerControlComponent.firing
    private val coolDown get() = playerControlComponent.cooldownRemaining


    private lateinit var rootTable: KTableWidget
    private lateinit var infoBoard: KTableWidget
    private lateinit var infoLabel: Label
    private lateinit var npcLabel: Label


    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
    override val stage = Stage(hudViewPort, batch)
        .apply {
            isDebugAll = debug
        }

    companion object {
        private const val aspectRatio = 16 / 9
        const val uiWidth = 400f
        const val uiHeight = uiWidth * aspectRatio
    }

    init {
        setup()
    }

    override fun update(delta: Float) {
        batch.projectionMatrix = stage.camera.combined

        updateInfo(delta)
        stage.act(delta)
        stage.draw()
    }

    private fun updateInfo(delta: Float) {
        infoLabel.setText(
            """
      AimVector: ${playerControlComponent.aimVector}

    """.trimIndent()
        )
    }

    /*
      MouseScreen: $mouseVector
      MouseWorld: ${controlMapper.mousePosition}
      AimVector: ${controlMapper.aimVector}
      ShotPos: ${transform.position.cpy().add(controlMapper.aimVector.cpy().scl(3f))}
      ShotVelocity: ${controlMapper.aimVector.cpy().scl(1000f)}

     */

    private val mouseVector = vec2()
    private fun getMousePosition(): Vector2 {
        mouseVector.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        return mouseVector
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
        stage.clear()
    }

    private fun setup() {
        stage.clear()
        setupInfo()
    }

    private fun setupInfo() {
        infoBoard = scene2d.table {
            label(
                """
Controls and stuff:
WASD -> Move
Mouse -> Aim & Shoot
      """
            )
            infoLabel = label("InfoLabel")
            npcLabel = label("NpcInfo")
        }

        rootTable = scene2d.table {
            setFillParent(true)
            bottom()
            left()
            add(infoBoard).expand().align(Align.bottomLeft)
            pad(10f)
        }

        stage.addActor(rootTable)
    }
}