package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import input.*
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import ktx.ashley.mapperFor

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

//    private fun toggleVehicle() {
//        if (player.entity.hasNot(mapperFor<IsInVehicleComponent>())) {
//            player.entity.add(EnterVehicleComponent())
//        } else {
//            player.entity.add(LeaveVehicleComponent())
//        }
//    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> keyboardControl.thrust = 0f
            Input.Keys.S -> keyboardControl.thrust = 0f
            Input.Keys.A -> keyboardControl.turning = 0f
            Input.Keys.D -> keyboardControl.turning = 0f
            Input.Keys.SPACE -> keyboardControl.firing = false
            else -> return false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (button == Input.Buttons.LEFT) {
            keyboardControl.firing = true
            true
        } else false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (!keyboardControl.firing) {
            keyboardControl.firing = true
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (button == Input.Buttons.LEFT) {
            keyboardControl.firing = false
            true
        } else false
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        keyboardControl = pccMapper[entity]
        updateMouseInput(tcMapper[entity].position)

    }

    private fun updateMouseInput(position: Vector2) {
        keyboardControl.setAimVector(Gdx.input.x, Gdx.input.y, position)

    }
}