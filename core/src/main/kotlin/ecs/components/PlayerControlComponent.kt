package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import injection.Context.inject
import ktx.math.vec2
import ktx.math.vec3
import tru.AnimState

class PlayerControlComponent(
    private val controlMapper: ControlMapper,
    private val rof: Float = 2f) : Component {

    private val camera : OrthographicCamera by lazy { inject() }
    var cooldownRemaining = 0f
        private set

    var shotDrawCoolDown = 0f
        private set


    var shotsFired = 0
        private set


    val playerAnimState : AnimState get() {
        return if(triggerPulled)
            AnimState.Aiming
        else if(moving)
            AnimState.Walk
        else AnimState.Idle
    }

    val drawShot get() = shotDrawCoolDown > 0f
    private val mousePosition3D = vec3()
    val triggerPulled get() = controlMapper.firing
    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aimVector get() = controlMapper.aimVector
    val mousePosition get() = controlMapper.mousePosition
    var latestHitPoint = vec2(0f,0f)
    val walkVector get() = if(triggerPulled) Vector2.Zero else controlMapper.walkVector.nor()
    val turning : Float get() { return if(stationary) 0f else controlMapper.turning }
    val walking : Float get()  { return if(stationary) 0f else controlMapper.thrust }
    val moving get() = walkVector.len2() != 0f
    var stationary = false

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



    fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        controlMapper.mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        controlMapper.aimVector.set(controlMapper.mousePosition).sub(position).nor()
    }


}