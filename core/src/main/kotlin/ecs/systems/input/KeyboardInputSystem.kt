package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerMode
import input.InputIndicator
import input.KeyboardControl
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import physics.getComponent

class KeyboardInputSystem :
    KtxInputAdapter, IteratingSystem(
    allOf(
        KeyboardControl::class,
        TransformComponent::class
    ).get()
) {
    lateinit var keyboardControl: KeyboardControl

    override fun keyDown(keycode: Int): Boolean {
        keyboardControl.aiming = false
        when (keycode) {
            Input.Keys.W -> keyboardControl.thrust = 1f
            Input.Keys.S -> keyboardControl.thrust = -1f
            Input.Keys.A -> keyboardControl.turning = -1f
            Input.Keys.D -> keyboardControl.turning = 1f
            else -> return false
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

    override fun keyUp(keycode: Int): Boolean {
        keyboardControl.aiming = true
        when (keycode) {
            Input.Keys.W -> keyboardControl.thrust = 0f
            Input.Keys.S -> keyboardControl.thrust = 0f
            Input.Keys.A -> keyboardControl.turning = 0f
            Input.Keys.D -> keyboardControl.turning = 0f
            Input.Keys.SPACE -> keyboardControl.doContextAction = true
            Input.Keys.R -> keyboardControl.needsReload = true
            Input.Keys.B -> toggleBuildMode()
            Input.Keys.LEFT -> keyboardControl.uiControl.left()
            Input.Keys.RIGHT -> keyboardControl.uiControl.right()
            Input.Keys.ENTER -> keyboardControl.uiControl.select()
            else -> return false
        }
        return true
    }

    private fun toggleBuildMode() {
        keyboardControl.isBuilding = !keyboardControl.isBuilding
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

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        keyboardControl = entity.getComponent()
        updateMouseInput(entity.getComponent<TransformComponent>().position)
    }

    private fun updateMouseInput(position: Vector2) {
        keyboardControl.setAimVector(Gdx.input.x, Gdx.input.y, position)

    }
}