package wastelandui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.Timer
import ktx.actors.keepWithinParent
import ktx.actors.onChange
import ktx.actors.onKey
import ktx.actors.txt
import ktx.scene2d.KTableWidget
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import statemachine.StateMachine
import story.conversation.IConversation
import tru.Assets

class ConversationPresenter(
    override val s: Stage,
    override val conversation: IConversation,
    override val conversationEnded: () -> Unit,
    private val showProtagonistPortrait: Boolean = true,
    private val showAntagonistPortrait: Boolean = true) : IConversationPresenter {
  private val speechBubbleNinePatch = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.font, Color.BLACK).apply { background = speechBubbleNinePatch }

  private val baseWidth = UserInterface.uiWidth / 2
  private val baseHeight = UserInterface.uiHeight / 3

  private var antagonistSpeechBubble: Label
  private var antagonistRoot: Table
	private var protagonistRoot: Table
	private var choiceTable: KTableWidget
	private var rootTable: KTableWidget

  val stateMachine : StateMachine<ConversationState, ConversationEvent> =
      StateMachine.buildStateMachine(ConversationState.NotStarted, ::stateChanged) {
        state(ConversationState.NotStarted) {
          edge(ConversationEvent.ConversationStarted, ConversationState.AntagonistIsSpeaking) {}
        }
        state(ConversationState.AntagonistIsSpeaking) {
          edge(ConversationEvent.AntagonistDoneTalking, ConversationState.ProtagonistChoosing) {}
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.ProtagonistChoosing) {
          edge(ConversationEvent.ProtagonistMadeAChoice, ConversationState.AntagonistIsSpeaking) {}
          edge(ConversationEvent.ProtagonistCouldNotChoose, ConversationState.CanConversationContinue) {}
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.CanConversationContinue) {
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.Ended) {}
      }

	init {

	  protagonistRoot = scene2d.table {
		  choiceTable = table {
			  background = speechBubbleNinePatch
			  keepWithinParent()
			  left()
			  bottom()
		  }.cell(expandY = true, width = baseWidth, align = Align.bottomRight, padLeft = 16f, padBottom = 2f)
		  if(showProtagonistPortrait) {
			  row()
			  image(Assets.portrait) {
				  setScaling(Scaling.fit)
				  keepWithinParent()
			  }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft, pad = 2f, colspan = 2)
		  }
		  isVisible = false
		  pack()
	  }

    antagonistRoot = scene2d.table {
	    antagonistSpeechBubble = label("", speechBubbleStyle) {
		    wrap = true
		    keepWithinParent()
	    }.cell(expandY = true, width = baseWidth, align = Align.bottomRight, padLeft = 16f, padBottom = 2f)
	    if(showAntagonistPortrait) {
		    row()
		    image(Assets.portrait) {
			    setScaling(Scaling.fit)
			    keepWithinParent()
		    }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft, pad = 2f, colspan = 2)
	    }
      isVisible = true
	    pack()
    }

		rootTable = scene2d.table {
			setFillParent(true)
			top()
			add(protagonistRoot).expand().align(Align.left)
			add(antagonistRoot).expand().align(Align.left)
		}

    s.addActor(rootTable)
    s.keyboardFocus = rootTable

    rootTable.onKey { key ->
      if (key.isDigit() && key in '0'..'9') {
        makeChoice(key.toNumber())
      }
    }
    stateMachine.initialize()
  }



  override fun dispose() {
	  s.actors.removeValue(rootTable, true)
  }

  private fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
	  choiceTable.clearChildren()
	  protagonistRoot.isVisible = true
	  choiceTable.apply {
		  protagonistChoices.withIndex().forEach { indexedValue ->
        val text = "${indexedValue.index}: ${indexedValue.value}"
			  val button = textButton(text)
        button.onChange {
          makeChoice(indexedValue.index)
        }
              button.label.wrap = true
			  add(button).align(Align.left).expandY().growX().pad(8f).space(4f).row()
			  button.keepWithinParent()
		  }
	  }
	  choiceTable.pack()
    choiceTable.isVisible = true
	  protagonistRoot.invalidate()
  }

  private fun showAntagonistLines(lines: Iterable<String>) {
    antagonistRoot.isVisible = true
    antagonistSpeechBubble.txt = ""
    Timer.instance().clear()
    var index = 0

    Timer.instance().scheduleTask(object: Timer.Task() {
      override fun run() {
        if(index < lines.count()) {
          antagonistSpeechBubble.text.append(lines.elementAt(index) + "\n")
	        antagonistSpeechBubble.invalidate()
	        index++
        } else {
          //Last time we're running, send done event!
          stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
        }
	      antagonistRoot.invalidate()
      }
    }, 0f, 2f, lines.count())
  }

  private fun makeChoice(index: Int) {
    if(stateMachine.currentState.state == ConversationState.ProtagonistChoosing &&
        conversation.protagonistCanChoose) {
      if(conversation.makeChoice(index)) {
        choiceTable.isVisible = false
        stateMachine.acceptEvent(ConversationEvent.ProtagonistMadeAChoice)
      }
    }

  }

  private fun stateChanged(state: ConversationState, event: ConversationEvent?) {
    when (state) {
      ConversationState.NotStarted -> stateMachine.acceptEvent(ConversationEvent.ConversationStarted)
      ConversationState.Ended -> conversationEnded()
      ConversationState.AntagonistIsSpeaking -> letTheManSpeak()
      ConversationState.ProtagonistChoosing -> makeTheManChoose()
      else -> checkIfWeAreDone()
    }
  }

  private fun checkIfWeAreDone() {
    stateMachine.acceptEvent(ConversationEvent.ConversationEnded)
  }

  private fun makeTheManChoose() {
    if(conversation.protagonistCanChoose)
      showProtagonistChoices(conversation.getProtagonistChoices())
    else
      stateMachine.acceptEvent(ConversationEvent.ProtagonistCouldNotChoose)
  }


  private fun letTheManSpeak() {
    choiceTable.isVisible = false
    if(conversation.antagonistCanSpeak)
      showAntagonistLines(conversation.getAntagonistLines())
    else
      stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
  }

  enum class ConversationEvent {
    ConversationStarted,
    AntagonistDoneTalking,
    ProtagonistMadeAChoice,
    ConversationEnded,
    ProtagonistCouldNotChoose
  }

  enum class ConversationState {
    NotStarted,
    Ended,
    AntagonistIsSpeaking,
    ProtagonistMustChoose,
    ProtagonistChoosing,
    CanConversationContinue
  }
}

