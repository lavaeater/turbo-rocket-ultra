package ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import control.ShipControl
import ecs.components.PlayerComponent
import ecs.components.TransformComponent
import injection.Context
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class UserInterface(
    private val batch: Batch,
    debug: Boolean = false): IUserInterface {

  override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
      .apply {
    isDebugAll = debug
  }

  companion object {
    private const val aspectRatio = 16 / 9
    const val uiWidth = 800f
    const val uiHeight = uiWidth * aspectRatio
  }

  private lateinit var infoLabel: Label
  private lateinit var npcLabel: Label

  init {
    setup()
  }

  override fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined

    updateInfo()
    stage.act(delta)
    stage.draw()
  }

  private fun updateInfo() {
    getMousePosition()
    infoLabel.setText("""
      Player: ${transform.position}
      MouseScreen: $mouseVector
      MouseWorld: ${shipControl.mousePosition}
      AimVector: ${shipControl.aimVector}
      ShotPos: ${transform.position.cpy().add(shipControl.aimVector.cpy().scl(3f))}
      ShotVelocity: ${shipControl.aimVector.cpy().scl(1000f)}
    """.trimIndent())
  }

  private val mouseVector = vec2()
  private fun getMousePosition() : Vector2 {
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

    setupCoronaStats()
  }

  private val shipControl: ShipControl by lazy { Context.inject() }
  private val engine: Engine by lazy { Context.inject() }
  private val playerEntity: Entity by lazy { engine.getEntitiesFor(allOf(PlayerComponent::class).get()).first() }
  private val transform: TransformComponent by lazy { playerEntity[mapperFor()]!! }

  private lateinit var rootTable: KTableWidget

  private lateinit var infoBoard: KTableWidget

  private fun setupCoronaStats() {
    infoBoard = scene2d.table {
      label("""
Controls and stuff:
WASD                    -> Control camera
Left and Right          -> Switch NPC to follow
c                       -> Stop following NPC
z                       -> Center camera //stop complaining
u, j                    -> zoom in and out
k, l                    -> rotate camera
r                       -> Reset Sim
      """)
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