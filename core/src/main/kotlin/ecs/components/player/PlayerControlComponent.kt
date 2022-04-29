package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import input.ControlMapper
import ktx.math.vec2
import tru.AnimState

/**
 * Make this into the one-stop shop of player state data.
 *
 * I have previously quite often done data by having different components and small components, I think that is actually
 * not the best idea anymore, but we learn as we go. So, Components can be slightly fatter, but still just data, and
 * the systems can be quite small and specific, but it is the data that is truly hard to keep track of. Having it in
 * the same place makes at least some things easier.
 */
class PlayerControlComponent(var controlMapper: ControlMapper) : Component, Pool.Poolable {
    var waitsForRespawn = false
    private var cooldownRemaining = 0f
    var rof: Float = 3f

    var shotsFired = 0
        private set

    val playerAnimState: AnimState
        get() {
            return when {
                waitsForRespawn -> AnimState.Death
                aiming -> AnimState.Aiming
                triggerPulled -> AnimState.Aiming
                moving -> AnimState.Walk
                else -> AnimState.Idle
            }
        }

    var needToChangeGun
        get() = controlMapper.needToChangeGun
        set(value) {
            controlMapper.needToChangeGun = value
        }
    var reloadStarted
    get() = controlMapper.needsReload
    set(value) { controlMapper.needsReload = value}

    var doContextAction
    get() = controlMapper.doContextAction
    set(value) {
        controlMapper.doContextAction = value
    }

    val triggerPulled get() = controlMapper.firing
    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aiming get() = controlMapper.aiming
    val aimVector get() = controlMapper.aimVector
    val mousePosition get() = controlMapper.mousePosition
    var latestHitPoint = vec2(0f, 0f)
    val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust).nor()
    val turning: Float
        get() {
            return if (playerMode != PlayerMode.Control) 0f else controlMapper.turning
        }
    val thrust: Float
        get() {
            return if (playerMode != PlayerMode.Control) 0f else controlMapper.thrust
        }
    val moving get() = walkVector.len2() != 0f
    var playerMode
        get() = controlMapper.playerMode
        set(value) {
            controlMapper.playerMode = value
        }

    fun coolDown(deltaTime: Float) {
        cooldownRemaining -= deltaTime
        cooldownRemaining = cooldownRemaining.coerceAtLeast(0f)
    }

    fun shoot() {
        shotsFired++
        cooldownRemaining += 1f / rof
    }

    fun setNewGun(newRof: Float) {
        rof = newRof
    }

    override fun reset() {
        cooldownRemaining = 0f
        shotsFired = 0
        latestHitPoint.setZero()
    }
}

