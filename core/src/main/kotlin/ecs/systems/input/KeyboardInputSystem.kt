package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import ecs.components.intent.IntendsTo
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import input.KeyboardControl
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import physics.getComponent
import physics.intendTo
import statemachine.StateMachine

class KeyboardInputSystem :
    KtxInputAdapter, IteratingSystem(
    allOf(
        KeyboardControl::class,
        TransformComponent::class
    ).get()
) {
    private var zoom = 0f
    lateinit var keyboardControl: KeyboardControl
    lateinit var keyboardEntity: Entity
    val inputActionHandler by lazy { inject<InputActionHandler>() }
    private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }

    override fun keyDown(keycode: Int): Boolean {
        if (::keyboardControl.isInitialized) {
            if (!keyboardControl.requireSequencePress) {
                when (keycode) {
                    Input.Keys.W -> keyboardControl.thrust = 1f
                    Input.Keys.S -> keyboardControl.thrust = -1f
                    Input.Keys.A -> keyboardControl.turning = -1f
                    Input.Keys.D -> keyboardControl.turning = 1f
                    Input.Keys.Z -> zoom = 1f
                    Input.Keys.X -> zoom = -1f
//TODO: Chaaange this madness!
                    Input.Keys.P -> if (gameState.currentState.state == GameState.Running) gameState.acceptEvent(
                        GameEvent.PausedGame
                    ) else gameState.acceptEvent(
                        GameEvent.ResumedGame
                    )
                    else -> if (gameState.currentState.state == GameState.Paused) gameState.acceptEvent(GameEvent.ResumedGame)
                            else
                                return false
                }
            }
        } else {
            when (keycode) {
                Input.Keys.P -> if (gameState.currentState.state == GameState.Running) gameState.acceptEvent(
                    GameEvent.PausedGame
                ) else gameState.acceptEvent(
                    GameEvent.ResumedGame
                )
                else -> return false
            }
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        if (::keyboardControl.isInitialized) {
            if (amountY > 0f) {
                keyboardEntity.intendTo(IntendsTo.SelectNextWeapon)
            } else if (amountY < 0f) {
                keyboardEntity.intendTo(IntendsTo.SelectPreviousWeapon)
            }
        }
        return true
    }

    val hackingKeys = listOf(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.ESCAPE)

    override fun keyUp(keycode: Int): Boolean {
        if (::keyboardControl.isInitialized) {
            if (keyboardControl.requireSequencePress && hackingKeys.contains(keycode)) {
                keyboardControl.keyPressedCallback(keycode)
            } else {
                when (keycode) {
                    Input.Keys.W -> keyboardControl.thrust = 0f
                    Input.Keys.S -> keyboardControl.thrust = 0f
                    Input.Keys.A -> keyboardControl.turning = 0f
                    Input.Keys.D -> keyboardControl.turning = 0f
                    Input.Keys.R -> keyboardEntity.intendTo(IntendsTo.Reload)
                    Input.Keys.B -> toggleBuildMode()
                    Input.Keys.LEFT -> handleLeft() //keyboardControl.uiControl.left()
                    Input.Keys.RIGHT -> handleRight() //keyboardControl.uiControl.right()
                    Input.Keys.ENTER -> handleSelect()//keyboardControl.uiControl.select()
                    Input.Keys.SPACE -> handleAction()
                    Input.Keys.Z -> zoom = 0f
                    Input.Keys.X -> zoom = 0f
                    else -> return false
                }
            }
        }
        return true
    }

    private fun handleSelect() {
        inputActionHandler.select(keyboardEntity)
    }

    private fun handleRight() {
        inputActionHandler.next(keyboardEntity)
    }

    private fun handleLeft() {
        inputActionHandler.previous(keyboardEntity)
    }

    private fun handleAction() {
        inputActionHandler.act(keyboardEntity)
    }

    private fun toggleBuildMode() {
        keyboardEntity.intendTo(IntendsTo.ToggleBuildMode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (::keyboardControl.isInitialized) {
            when (button) {
                Input.Buttons.LEFT -> {
                    keyboardControl.firing = true
                    keyboardControl.aiming = true
                    true
                }
                else -> false
            }
        } else false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (::keyboardControl.isInitialized) {
            if (!keyboardControl.firing) {
                keyboardControl.firing = true
                keyboardControl.aiming = true
            }
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (::keyboardControl.isInitialized) {
            when (button) {
                Input.Buttons.LEFT -> {
                    keyboardControl.firing = false
                    keyboardControl.aiming = false
                    true
                }
                else -> false
            }
        } else false
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        keyboardEntity = entity
        keyboardControl = entity.getComponent()
        updateMouseInput(entity.getComponent<TransformComponent>().position)
        camera.zoom += 0.1f * zoom
    }
    val camera by lazy { inject<OrthographicCamera>() }

    private fun updateMouseInput(position: Vector2) {
        keyboardControl.setAimVector(Gdx.input.x, Gdx.input.y, position)
    }
}