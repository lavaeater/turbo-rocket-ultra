package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import input.ControlMapper
import ktx.math.vec2
import ktx.math.vec3
import tru.AnimState

class PlayerControlComponent(var controlMapper: ControlMapper) : Component, Pool.Poolable {
    private var cooldownRemaining = 0f
    private val rof: Float = 3f

    var shotDrawCoolDown = 0f
        private set

    var shotsFired = 0
        private set

    val playerAnimState : AnimState get() {
        return when {
            aiming -> AnimState.Aiming
            triggerPulled -> AnimState.Aiming
            moving -> AnimState.Walk
            else -> AnimState.Idle
        }
    }

    val drawShot get() = shotDrawCoolDown > 0f
    private val mousePosition3D = vec3()
    val triggerPulled get() = controlMapper.firing
    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aiming get() = controlMapper.aiming
    val aimVector get() = controlMapper.aimVector
    val mousePosition get() = controlMapper.mousePosition
    var latestHitPoint = vec2(0f,0f)
    val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust).nor()
    val turning : Float get() { return if(playerMode != PlayerMode.Control) 0f else controlMapper.turning }
    val thrust : Float get()  { return if(playerMode != PlayerMode.Control) 0f else controlMapper.thrust }
    val moving get() = walkVector.len2() != 0f
    var playerMode get() = controlMapper.playerMode
        set(value) {
            controlMapper.playerMode = value
        }

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

    override fun reset() {
        cooldownRemaining = 0f
        shotsFired = 0
        shotDrawCoolDown = 0f
        latestHitPoint.setZero()

    }
}

