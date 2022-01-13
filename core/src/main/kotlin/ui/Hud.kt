package ui

import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Align.topLeft
import com.badlogic.gdx.utils.Queue
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.Players
import ecs.components.player.ComplexActionComponent
import ecs.components.player.PlayerControlComponent
import injection.Context.inject
import ktx.actors.*
import ktx.math.vec2
import ktx.math.vec3
import ktx.scene2d.*
import physics.AshleyMappers
import story.FactsOfTheWorld
import story.fact.Facts
import ui.customactors.boundLabel
import ui.customactors.boundProgressBar
import ui.customactors.repeatingTexture
import ui.customactors.typingLabel
import kotlin.reflect.KClass


class Hud(private val batch: Batch) : IUserInterface, MessageReceiver {
    private val aspectRatio = 14f / 9f
    private val hudWidth = 960f
    private val hudHeight = hudWidth * aspectRatio
    private val camera = OrthographicCamera()
    override val hudViewPort = ExtendViewport(hudWidth, hudHeight, camera)
    private val worldCamera by lazy { inject<OrthographicCamera>() }
    private val audioPlayer by lazy { inject<AudioPlayer>() }
    private val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }


    private val projectionVector = vec3()
    private val _projectionVector = vec2()
    private val projection2d: Vector2
        get() {
            _projectionVector.set(projectionVector.x, projectionVector.y)
            return _projectionVector
        }

    override fun hide() {
    }

    lateinit var killCountLabel: Label

    private val stage by lazy {
        val aStage = Stage(hudViewPort, batch)
        aStage.isDebugAll = true
        aStage.actors {
            table {
                setFillParent(true)
                top()
                table {
                    setFillParent(true)
                    killCountLabel =
                        boundLabel({
                            "Kill Count: ${factsOfTheWorld.getIntValue(Facts.EnemyKillCount)} / ${
                                factsOfTheWorld.getIntValue(
                                    Facts.TargetEnemyKillCount
                                )
                            }"
                        }) {
                            isVisible = factsOfTheWorld.getBooleanFact(Facts.ShowEnemyKillCount).value
                        }
                }
                row()
                table {
                    for ((control, player) in Players.players) {
                        table {
                            width = aStage.width / 8
                            label("${control.controllerId}").inCell.align(Align.right).width(aStage.width / 8)
                            row()
                            boundLabel({ "Kills: ${player.kills}" }).inCell.align(Align.right)
                            row()
                            boundLabel({ "Objectives: ${player.touchedObjectives.count()}" }).inCell.align(Align.right)
                            row()
                            boundLabel({ "Score: ${player.score}" }).inCell.align(Align.right)
                            row()
                            boundLabel({ "${player.currentWeapon}: ${player.ammoLeft}|${player.totalAmmo}" }).inCell.align(
                                Align.right
                            )
                            row()
                            boundProgressBar(
                                { player.health },
                                0f,
                                player.startingHealth,
                                0.1f
                            ) { }.inCell.align(Align.right)
                            row()
                            repeatingTexture(
                                { player.lives },
                                5f,
                                AshleyMappers.animatedCharacter.get(player.entity).currentAnim.keyFrames.first()
                            ) {}
                        }
                    }
                }
                left()
                bottom()
            }
        }
        aStage
    }


    var isReady = false
    override fun show() {
        //Set up this as receiver for messages with messagehandler
        inject<MessageHandler>().apply {
            this.receivers.add(this@Hud)
        }
    }

    override fun update(delta: Float) {
        showToasts(delta)
        stage.act(delta)
        stage.draw()
        killCountLabel.isVisible = factsOfTheWorld.getBooleanFact(Facts.ShowEnemyKillCount).value
        isReady = true
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
    }

    override fun reset() {
    }

    private val pauseBlurb by lazy {
        val dialogWidth = 800f
        val dialogHeight = 800f
        val x = stage.width / 2 - dialogWidth / 2
        val y = stage.height / 4 + dialogHeight / 2
        val text = """
    So, can it do line feeds? Can it handle end of column and wrapping and stuff like that?
    Will this actually work?
    Can this be the coolness?
""".trimIndent()

        val d = scene2d.dialog("Paused") {
            contentTable.add(
                scene2d.table {
                    setFillParent(true)
                    left()
                    top()
                    typingLabel(text)//.inCell.expand()
                })
            width = dialogWidth
            height = dialogHeight
            //contentTable.pack()
            pack()
            setPosition(x, y)
        }
        stage.addActor(d)
        d
    }

    override fun pause() {
        pauseBlurb.isVisible = true
    }

    override fun resume() {
        if (isReady)
            pauseBlurb.isVisible = false
    }

    /***
     * This is a story method, this should be generalized to support more
     * dynamic stuff.
     */
    override val messageTypes: Set<KClass<*>> =
        setOf(Message.ShowToast::class, Message.ShowUiForComplexAction::class, Message.ShowProgressBar::class)

    fun worldToHudPosition(worldPosition: Vector2): Vector2 {
        projectionVector.set(worldPosition.x, worldPosition.y, 0f)
        worldCamera.project(projectionVector)
        projectionVector.set(projectionVector.x, Gdx.graphics.height - projectionVector.y, projectionVector.z)
        camera.unproject(projectionVector)
        return projection2d.cpy()
    }

    /*
    Show toasts in order received, for one second each, using a specific style for the
    label - and also scale it to twice the size or add some kind of anim to it.
     */
    private val toastQueue = Queue<Message.ShowToast>()
    private var toastCooldown = 0.0f

    fun showToasts(delta: Float) {
        toastCooldown -= delta
        if (toastQueue.any() && toastCooldown <= 0f) {
            toastCooldown = 1f
            val toastToShow = toastQueue.removeFirst()
            val coordinate = worldToHudPosition(toastToShow.worldPosition)
            val moveAction = object : Action() {
                override fun act(delta: Float): Boolean {
                    val coordinate = worldToHudPosition(toastToShow.worldPosition)
                    actor.setPosition(coordinate.x, coordinate.y)
                    return true
                }
            }
            val sequence = (delay(1f).along(moveAction)).then(removeActor())
            stage.actors {
                label(toastToShow.toast, "title") { actor ->
                    actor += sequence
                }.setPosition(coordinate.x, coordinate.y)
            }
        }
    }

    fun addProgressBar(progressBar: Message.ShowProgressBar) {
        val coordinate = worldToHudPosition(progressBar.worldPosition)
        val moveAction = object : Action() {
            override fun act(delta: Float): Boolean {
                val c = worldToHudPosition(progressBar.worldPosition)
                actor.setPosition(c.x, c.y)
                return true
            }
        }
        val sequence = (delay(progressBar.maxTime).along(moveAction)).then(removeActor())
        stage.actors {
            boundProgressBar(progressBar.progress, 0f, progressBar.maxTime) {
                this += sequence
            }.setPosition(coordinate.x, coordinate.y)
        }
    }

    override fun recieveMessage(message: Message) {
        when (message) {
            is Message.ShowToast -> {
                toastQueue.addLast(message)
            }
            is Message.ShowUiForComplexAction -> {
                val moveAction = object : Action() {
                    override fun act(delta: Float): Boolean {
                        val coordinate = worldToHudPosition(message.worldPosition)
                        actor.setPosition(coordinate.x, coordinate.y)
                        return true
                    }
                }
                val coordinate = worldToHudPosition(message.worldPosition)
                message.complexActionComponent.doneCallBacks.add {
                    stage.actors.removeValue(message.complexActionComponent.scene2dTable, true)
                }
                val sequence = repeat(10000, moveAction)
                stage.addActor(message.complexActionComponent.scene2dTable.apply {
                    this += sequence
                    this.setPosition(coordinate.x, coordinate.y)
                })
            }
            is Message.ShowProgressBar -> {
                addProgressBar(message)
            }
        }
    }
}


interface MessageReceiver {
    val messageTypes: Set<KClass<*>>
    fun recieveMessage(message: Message)
}

class MessageHandler {
    // QUeue not used for now, but perhaps later, much later?
    val messageQueue = Queue<Message>()
    val receivers = mutableListOf<MessageReceiver>()
    fun sendMessage(message: Message) {
        val validReceivers = receivers.filter { it.messageTypes.contains<KClass<out Any>>(message::class) }
        for (receiver in validReceivers) {
            receiver.recieveMessage(message)
        }
    }
}

sealed class Message {
    class ShowProgressBar(val maxTime: Float, val worldPosition: Vector2, val progress: () -> Float) : Message()
    class ShowToast(val toast: String, val worldPosition: Vector2) : Message()
    class ShowUiForComplexAction(
        val complexActionComponent: ComplexActionComponent,
        val controlComponent: PlayerControlComponent,
        val worldPosition: Vector2
    ) : Message()
}