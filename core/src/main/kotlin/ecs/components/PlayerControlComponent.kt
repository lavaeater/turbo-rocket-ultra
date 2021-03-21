package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import injection.Context.inject
import ktx.math.vec2
import ktx.math.vec3

class PlayerControlComponent(
    private val controlMapper: ControlMapper,
    val rof: Float = 10f) : Component {

    fun coolDown(deltaTime: Float) {
        cooldownRemaining-=deltaTime
        shotDrawCoolDown-=deltaTime
        cooldownRemaining = cooldownRemaining.coerceAtLeast(0f)
        shotDrawCoolDown = shotDrawCoolDown.coerceAtLeast(0f)
    }

    fun shoot() {
        shotsFired++
        cooldownRemaining += 1f/rof
        shotDrawCoolDown = cooldownRemaining / 4
    }

    private val camera : OrthographicCamera by lazy { inject() }

    var cooldownRemaining = 0f
    private set

    var shotDrawCoolDown = 0f
    private set

    val drawShot get() = shotDrawCoolDown > 0f

    var shotsFired = 0
    private set

    private val mousePosition3D = vec3()

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {

        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)

        camera.unproject(mousePosition3D)

        controlMapper.mousePosition.set(mousePosition3D.x, mousePosition3D.y)

        aimVector.set(controlMapper.mousePosition).sub(position).nor()
    }


    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aimVector get() = controlMapper.aimVector
    val mousePosition get() = controlMapper.mousePosition
    var latestHitPoint = vec2(0f,0f)
    val turning : Float get() { return if(stationary) 0f else controlMapper.turning }
    val walking : Float get()  { return if(stationary) 0f else controlMapper.thrust }
    var stationary = false
}