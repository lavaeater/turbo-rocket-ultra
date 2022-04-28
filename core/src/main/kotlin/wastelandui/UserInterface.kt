package wastelandui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.actors.keepWithinParent
import ktx.scene2d.KTableWidget
import ktx.scene2d.scene2d
import ktx.scene2d.table
import statemachine.StateMachine
import story.conversation.IConversation
import tru.Assets
import turbofacts.Factoids
import turbofacts.NewFactsOfTheWorld

class UserInterface(
  private val batch: Batch,
  private val gameState: StateMachine<GameState, GameEvent>,
  private val inputManager: InputMultiplexer,
  private val factsOfTheWorld: NewFactsOfTheWorld,
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

  private lateinit var conversationUi: IConversationPresenter
  private val labelStyle = Label.LabelStyle(Assets.font, Color.WHITE)
  private lateinit var scoreLabel: Label


  override fun runConversation(
      conversation: IConversation,
      conversationEnded: () -> Unit,
      showProtagonistPortrait: Boolean,
      showAntagonistPortrait:Boolean) {

    conversationUi = ConversationPresenter(
        stage,
        conversation, {
      conversationUi.dispose()
      conversationEnded()
    },
        showProtagonistPortrait,
        showAntagonistPortrait)
  }

  init {
    setup()
  }

  override fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined
    updateScore()
    stage.act(delta)
    stage.draw()
  }

  private var score = 0

  private fun updateScore() {
    val tempScore = factsOfTheWorld.getInt(Factoids.Score)
    if (tempScore != score) {
      score = tempScore
      scoreLabel.setText("Score: $score")
    }
  }

  override fun dispose() {
    stage.dispose()
  }

  override fun clear() {
    stage.clear()
  }


  private fun setup() {
    stage.clear()
    inputManager.addProcessor(stage)

    setUpScoreBoard()
  }

  private lateinit var rootTable: KTableWidget

  private lateinit var scoreBoard: KTableWidget

  private fun setUpScoreBoard() {
    scoreBoard = scene2d.table {
      scoreLabel = label("Score: $score", labelStyle) {
        setWrap(true)
        keepWithinParent()
      }.cell(fill = true, align = Align.bottomLeft, padLeft = 16f, padBottom = 2f)
      isVisible = true
      pack()
      width = 300f

    }

    rootTable = scene2d.table {
      setFillParent(true)
      bottom()
      left()
      add(scoreBoard).expand().align(Align.bottomLeft)
    }

    stage.addActor(rootTable)
  }

  override fun showSplashScreen() {
    /*
    Set up timer. Show splash screen.
    When timer fires, remove splash screen, send resume game event. Yay!
     */

//    val splashScreen = scene2table {
//      image(Assets.splashScreen) {
//        setScaling(Scaling.fit)
//        scaleBy(4.0f)
//      }.cell()
//      setFillParent(true)
//      isVisible = true
//      bottom()
//      left()
//    }
//    stage.addActor(splashScreen)
//    Timer.instance().clear()
//
//    Timer.instance().scheduleTask(object : Timer.Task() {
//      override fun run() {
//        stage.actors.removeValue(splashScreen, true)
//        gameState.handleEvent(GameEvents.GameResumed)
//      }
//    }, 3f)
//  }
  }


  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}