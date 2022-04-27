package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import ecs.components.intent.IntendsTo
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import input.InputIndicator
import input.KeyboardControl
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import physics.build
import physics.getComponent
import physics.intendTo
import physics.isBuilding
import statemachine.StateMachine

class ActionHandler {
    /**
     * Handles spaceBar, for instance
     */
    fun next(entity: Entity) {
        if(entity.isBuilding()) {
            entity.build().buildables.nextItem()
        }
    }

    fun previous(entity: Entity) {
        if(entity.isBuilding()) {
            entity.build().buildables.previousItem()
        }
    }

    /**
     * Depending on mode, selects something - if in buildMode, it will simply BUILD
     */
    fun select(entity: Entity) {
        if(entity.isBuilding()) {
            entity.intendTo(IntendsTo.Build)
        }
    }

    fun act(entity: Entity) {
        if(entity.isBuilding()) {
            entity.intendTo(IntendsTo.Build)
        }
    }
}

class KeyboardInputSystem :
    KtxInputAdapter, IteratingSystem(
    allOf(
        KeyboardControl::class,
        TransformComponent::class
    ).get()
) {
    lateinit var keyboardControl: KeyboardControl
    lateinit var keyboardEntity: Entity
    val actionHandler by lazy { inject<ActionHandler>() }
    val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }

    override fun keyDown(keycode: Int): Boolean {
        keyboardControl.aiming = false
        if (!keyboardControl.requireSequencePress) {
            when (keycode) {
                Input.Keys.W -> keyboardControl.thrust = 1f
                Input.Keys.S -> keyboardControl.thrust = -1f
                Input.Keys.A -> keyboardControl.turning = -1f
                Input.Keys.D -> keyboardControl.turning = 1f
                Input.Keys.P -> if (gameState.currentState.state == GameState.Running) gameState.acceptEvent(GameEvent.PausedGame) else gameState.acceptEvent(
                    GameEvent.ResumedGame
                )
                else -> return false
            }
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        if (amountY > 0f) {
            keyboardControl.needToChangeGun = InputIndicator.Next
        } else if (amountY < 0f) {
            keyboardControl.needToChangeGun = InputIndicator.Previous
        }
        return true
    }

    val hackingKeys = listOf(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.ESCAPE)

    override fun keyUp(keycode: Int): Boolean {
        if (keyboardControl.requireSequencePress && hackingKeys.contains(keycode)) {
            keyboardControl.keyPressedCallback(keycode)
        } else {
            keyboardControl.aiming = true
            when (keycode) {
                Input.Keys.W -> keyboardControl.thrust = 0f
                Input.Keys.S -> keyboardControl.thrust = 0f
                Input.Keys.A -> keyboardControl.turning = 0f
                Input.Keys.D -> keyboardControl.turning = 0f
                Input.Keys.R -> keyboardControl.needsReload = true
                Input.Keys.B -> toggleBuildMode()
                Input.Keys.LEFT -> handleLeft() //keyboardControl.uiControl.left()
                Input.Keys.RIGHT -> handleRight() //keyboardControl.uiControl.right()
                Input.Keys.ENTER -> handleSelect()//keyboardControl.uiControl.select()
                Input.Keys.SPACE -> handleAction()
                else -> return false
            }
        }
        return true
    }

    private fun handleSelect() {
        actionHandler.select(keyboardEntity)
    }

    private fun handleRight() {
        actionHandler.next(keyboardEntity)
    }

    private fun handleLeft() {
        actionHandler.previous(keyboardEntity)
    }

    private fun handleAction() {
        actionHandler.act(keyboardEntity)
    }

    private fun toggleBuildMode() {
        //Fucking finally
        keyboardEntity.intendTo(IntendsTo.ToggleBuildMode)
        //keyboardControl.isInBuildMode = !keyboardControl.isInBuildMode
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Input.Buttons.LEFT -> {
                keyboardControl.firing = true
                keyboardControl.aiming = true
                true
            }
            else -> false
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (!keyboardControl.firing) {
            keyboardControl.firing = true
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Input.Buttons.LEFT -> {
                keyboardControl.firing = false
                keyboardControl.aiming = false
                true
            }
            else -> false
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        keyboardEntity = entity
        keyboardControl = entity.getComponent()
        updateMouseInput(entity.getComponent<TransformComponent>().position)
    }

    private fun updateMouseInput(position: Vector2) {
        keyboardControl.setAimVector(Gdx.input.x, Gdx.input.y, position)
    }
}