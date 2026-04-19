package screens

import ai.arena.EvolutionLoop
import ai.arena.FitnessFunction
import ai.arena.Population
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.FitViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.actors.onClick
import ktx.scene2d.*
import statemachine.StateMachine
import animation.Assets
import ktx.scene2d.Scene2DSkin
import kotlin.concurrent.thread

/**
 * Three-panel screen for driving the mutation evolution loop.
 *
 * Left:   population list (generation + scored tree list)
 * Center: controls (run one generation headlessly, load/save, stop)
 * Right:  fitness weight tuning + log
 *
 * Access via SetupScreen → press M.
 */
class MutatorArenaScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    override val camera = OrthographicCamera().apply { setToOrtho(false) }
    override val viewport = FitViewport(1600f, 900f, camera)

    private val skin get() = Scene2DSkin.defaultSkin
    private val evolutionLoop = EvolutionLoop(
        populationSize = 20,
        eliteCount = 4,
        simDuration = 30f,
        runsPerCandidate = 3,
        onProgress = { done, total -> statusLine = "Evaluating $done / $total …" }
    )

    private var population: Population? = null
    private var running = false
    private var statusLine = "Idle"
    private var log = mutableListOf<String>()

    // ──────────────────────────── UI ────────────────────────────────────

    private val stage by lazy {
        Stage(FitViewport(1600f, 900f, OrthographicCamera()), batch).also {
            Gdx.input.inputProcessor = it
            buildUi(it)
        }
    }

    private lateinit var statusLabel: Label
    private lateinit var populationTable: Table
    private lateinit var logLabel: Label

    private fun buildUi(stage: Stage) {
        stage.actors {
            table {
                setFillParent(true)
                pad(16f)

                // ── Left: population list ──
                table {
                    top().left()
                    label("POPULATION", "title") { it.colspan(1).pad(4f).row() }
                    this@MutatorArenaScreen.populationTable = table { it.expandY().fillY().row() }
                }.cell(expandY = true, fillY = true, width = 400f, padRight = 16f)

                // ── Center: controls ──
                table {
                    top()
                    label("CONTROLS", "title") { it.pad(4f).row() }

                    textButton("Seed Initial Population") {
                        onClick {
                            if (!running) seedPopulation()
                        }
                        it.pad(4f).fillX().row()
                    }
                    textButton("Run One Generation") {
                        onClick {
                            if (!running) runOneGeneration()
                        }
                        it.pad(4f).fillX().row()
                    }
                    textButton("Back to Menu") {
                        onClick { gameState.acceptEvent(GameEvent.ExitMutatorArena) }
                        it.pad(4f).fillX().row()
                    }

                    this@MutatorArenaScreen.statusLabel =
                        label("Idle") { it.pad(8f).row() }

                }.cell(expandY = true, fillY = true, width = 300f, padRight = 16f)

                // ── Right: log + weights ──
                table {
                    top().left()
                    label("FITNESS WEIGHTS", "title") { it.colspan(2).pad(4f).row() }
                    label("Damage dealt × ${FitnessFunction.weights.damageDealt}") { it.row() }
                    label("Kill bonus = ${FitnessFunction.weights.playerKillBonus}") { it.row() }
                    label("Survival × ${FitnessFunction.weights.survivalTime}") { it.row() }
                    label("Damage taken penalty × ${FitnessFunction.weights.damageTakenPenalty}") { it.row() }
                    label("") { it.row() }
                    label("LOG", "title") { it.colspan(2).pad(4f).row() }
                    this@MutatorArenaScreen.logLabel = label("") { it.left().row() }
                }.cell(expand = true, fill = true)
            }
        }
    }

    // ──────────────────────────── Actions ───────────────────────────────

    private fun seedPopulation() {
        running = true
        statusLine = "Seeding…"
        thread(isDaemon = true) {
            population = evolutionLoop.seedInitial()
            running = false
            statusLine = "Seeded generation 0 (${population!!.candidates.size} trees)"
            appendLog("Gen 0 seeded.")
        }
    }

    private fun runOneGeneration() {
        val pop = population ?: run { statusLine = "Seed a population first!"; return }
        running = true
        thread(isDaemon = true) {
            val next = evolutionLoop.runGeneration(pop)
            population = next
            running = false
            val best = next.bestScore
            statusLine = "Gen ${next.generation} done. Best = ${best.toInt()}"
            appendLog("Gen ${next.generation}: best score ${best.toInt()}")
        }
    }

    private fun appendLog(line: String) {
        log.add(line)
        if (log.size > 20) log.removeAt(0)
    }

    // ──────────────────────────── Render ────────────────────────────────

    override fun render(delta: Float) {
        super.render(delta)

        // Update mutable labels each frame (thread-safe string reads)
        if (::statusLabel.isInitialized) statusLabel.setText(statusLine)
        if (::logLabel.isInitialized) logLabel.setText(log.joinToString("\n"))
        if (::populationTable.isInitialized) {
            populationTable.clear()
            population?.candidates?.sortedByDescending { it.score }?.forEachIndexed { i, c ->
                populationTable.add(Label("#${i + 1} score ${c.score.toInt()}", skin)).left().row()
            }
        }

        stage.act(delta)
        stage.draw()
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        super.dispose()
    }
}
