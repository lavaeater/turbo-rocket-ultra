package ui

import ecs.components.ai.SeenPlayerPositions
import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Queue
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.Players
import ecs.components.enemy.AttackableProperties
import ecs.systems.graphics.GameConstants
import factories.factsOfTheWorld
import injection.Context.inject
import ktx.actors.along
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.math.vec2
import ktx.math.vec3
import ktx.scene2d.*
import messaging.Message
import messaging.MessageHandler
import messaging.MessageReceiver
import physics.*
import turbofacts.Factoids
import ui.customactors.boundLabel
import ui.customactors.boundProgressBar
import ui.customactors.repeatingTexture
import kotlin.reflect.KClass


class Hud(private val batch: Batch, debugAll: Boolean) : IUserInterface, MessageReceiver {
    private val aspectRatio = 14f / 9f
    private val hudWidth = 720f
    private val hudHeight = hudWidth * aspectRatio
    private val camera = OrthographicCamera()
    override val hudViewPort = ExtendViewport(hudWidth, hudHeight, camera)
    private val worldCamera by lazy { inject<OrthographicCamera>() }
    private val audioPlayer by lazy { inject<AudioPlayer>() }
    private val factsOfTheWorld by lazy { factsOfTheWorld() }

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

    override val stage by lazy {
        val aStage = Stage(hudViewPort, batch)
        aStage.isDebugAll = debugAll
        aStage
    }

    var isReady = false
    var needsPlayerInfos = true
    override fun show() {
        //Set up this as receiver for messages with messagehandler
        inject<MessageHandler>().addReceiver(this@Hud)
        if (needsPlayerInfos)
            addPlayerInfos()
    }

    fun addPlayerInfos() {
        stage.actors {
            table {
                setFillParent(true)
                top()
                table {
                    setFillParent(true)
                    killCountLabel =
                        boundLabel({
                            "Kill Count: ${factsOfTheWorld.getInt(Factoids.EnemyKillCount)} / ${
                                factsOfTheWorld.getInt(Factoids.TargetEnemyKillCount)
                            }"
                        }) {
                            isVisible = factsOfTheWorld.getBooleanFact(Factoids.ShowEnemyKillCount).value
                        }
                }
                row()
                table {
                    table {
                        width = stage.width / 8
                        label("Level Info")
                        row()
                        boundLabel({
                            factsOfTheWorld.getString(Factoids.CurrentMapName)
                        })
                    }
                    for ((control, player) in Players.players) {
                        val e = player.entity
                        table {
                            width = stage.width / 8
                            label(control.controllerId).inCell.align(Align.right).width(stage.width / 8)
                            row()
                            boundLabel({
                                if (e.has<SeenPlayerPositions>())
                                    e.getComponent<SeenPlayerPositions>().storage.joinToString(" | ")
                                else
                                    "Nothing"
                            })
//                            row()
//                            boundLabel({ "Kills: ${player.kills}" }).inCell.align(Align.right)
//                            row()
//                            boundLabel({ "Objectives: ${player.touchedObjectives.count()}" }).inCell.align(Align.right)
//                            row()
//                            boundLabel({ "Score: ${player.score}" }).inCell.align(Align.right)
//                            row()
//                            boundLabel({ "Speed: ${if(player.isReady) {
//                                player.entity.playerControl().actualSpeed
//                            } else 0f}" }).inCell.align(Align.right)
                            row()
                            boundLabel({ player.currentWeapon }).inCell.align(
                                Align.right
                            )
                            row()
                            boundLabel({ "${player.ammoLeft}|${player.totalAmmo}" }).inCell.align(
                                Align.right
                            )
                            row()
                            boundProgressBar(
                                { player.entity.getComponent<AttackableProperties>().health },
                                0f,
                                GameConstants.BASE_HEALTH,
                                0.1f
                            ) { }.inCell.align(Align.right)
                            row()
                            repeatingTexture(
                                { player.lives },
                                5f,
                                Sprite(player.entity.animation().currentAnim.keyFrames.first()).apply {
                                    flip(
                                        false,
                                        true
                                    )
                                }
                            ) {}
                        }
                    }
                }
                left()
                bottom()
            }
        }
    }

    override fun update(delta: Float) {
        showToasts(delta)
        stage.act(delta)
        stage.draw()
        killCountLabel.isVisible = factsOfTheWorld.getBooleanFact(Factoids.ShowEnemyKillCount).value
        isReady = true
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
    }

    override fun reset() {
    }

    private lateinit var pauseDialog: KDialog

    private val pauseBlurb by lazy {
        pauseDialog = scene2d.dialog("Paused") {
            contentTable.add(
                label("Press any key...")
            )
        }
        stage.addActor(pauseDialog)
        pauseDialog
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
        setOf(
            Message.ShowToast::class,
            Message.ShowUiForComplexAction::class,
            Message.ShowProgressBar::class,
            Message.LevelStarting::class,
            Message.LevelComplete::class,
            Message.LevelFailed::class
        )

    override fun worldToHudPosition(worldPosition: Vector2): Vector2 {
        projectionVector.set(worldPosition.x, worldPosition.y, 0f)
        worldCamera.project(projectionVector)
        projectionVector.set(projectionVector.x, Gdx.graphics.height - projectionVector.y, projectionVector.z)
        camera.unproject(projectionVector)
        return projection2d.cpy()
    }

    fun getMoveableDialog() {

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

    override fun receiveMessage(message: Message) {
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
            is Message.FactUpdated -> TODO() //We don't subscribe to this type of messages so this won't happen
            is Message.LevelComplete -> {
                showPauseCrawl(message.completeMessage)
            }
            is Message.LevelFailed -> {
                showPauseCrawl(message.failMessage)
            }
            is Message.LevelStarting -> {
                showPauseCrawl(message.beforeStartMessage)
            }
        }
    }

    fun showPauseCrawl(text: String) {
        if(::pauseDialog.isInitialized)
            CrawlDialog.showDialog(pauseDialog, text)
    }
}

