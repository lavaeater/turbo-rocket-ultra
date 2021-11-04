package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import ecs.components.player.GunFrames
import ecs.components.player.PlayerMode
import ecs.components.player.WeaponComponent
import input.*
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.getComponent

class KeyboardInputSystem:
    KtxInputAdapter, IteratingSystem(
    allOf(
        KeyboardControl::class,
        TransformComponent::class).get()) {

    lateinit var keyboardControl: KeyboardControl
    private val pccMapper = mapperFor<KeyboardControl>()
    private val tcMapper = mapperFor<TransformComponent>()

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> keyboardControl.thrust = 1f
            Input.Keys.S -> keyboardControl.thrust = -1f
            Input.Keys.A -> keyboardControl.turning = -1f
            Input.Keys.D -> keyboardControl.turning = 1f
            Input.Keys.SPACE -> keyboardControl.firing = true
            else -> return false
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> keyboardControl.thrust = 0f
            Input.Keys.S -> keyboardControl.thrust = 0f
            Input.Keys.A -> keyboardControl.turning = 0f
            Input.Keys.D -> keyboardControl.turning = 0f
            Input.Keys.SPACE -> keyboardControl.firing = false
            Input.Keys.B -> toggleBuildMode()
            Input.Keys.LEFT -> keyboardControl.uiControl.left()
            Input.Keys.RIGHT -> keyboardControl.uiControl.right()
            Input.Keys.ENTER -> keyboardControl.uiControl.select()
            else -> return false
        }
        return true
    }

    private fun toggleBuildMode() {
        when(keyboardControl.playerMode) {
            PlayerMode.Control -> keyboardControl.playerMode = PlayerMode.Building
            PlayerMode.Building -> {
                keyboardControl.uiControl.cancel()
                keyboardControl.playerMode = PlayerMode.Control
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when(button)  {
            Input.Buttons.LEFT ->  {
                keyboardControl.firing = true
                keyboardControl.aiming = true
                true
            }
            Input.Buttons.RIGHT -> {
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
        return when(button)  {
            Input.Buttons.LEFT ->  {
                keyboardControl.firing = false
                keyboardControl.aiming = false
                true
            }
            Input.Buttons.RIGHT -> {
                changeGun()
                true
            }
            else -> false
        }
    }

    private fun changeGun() {
        keyboardControl.needToChangeGun = true
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        keyboardControl = pccMapper[entity]
        updateMouseInput(tcMapper[entity].position)
        if(keyboardControl.needToChangeGun) {
            keyboardControl.needToChangeGun = false
            val weaponComponent = entity.getComponent<WeaponComponent>()
            when(weaponComponent.currentGun) {
                GunFrames.handGun -> weaponComponent.currentGun = GunFrames.spas12
                else -> weaponComponent.currentGun = GunFrames.handGun
            }
        }

    }

    private fun updateMouseInput(position: Vector2) {
        keyboardControl.setAimVector(Gdx.input.x, Gdx.input.y, position)

    }
}