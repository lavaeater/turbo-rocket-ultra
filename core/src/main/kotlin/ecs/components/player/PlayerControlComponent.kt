package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import data.Player
import ecs.systems.graphics.CompassDirection
import ecs.systems.graphics.GameConstants
import input.ControlMapper
import ktx.ashley.mapperFor
import ktx.math.vec2
import tru.AnimState
import kotlin.reflect.KMutableProperty

/**
 * The ideas expressed below are wrong. We shall have a sea of components,
 * absolutely drowning everything in components, and they shall be small and specific, it is quite
 * glorious.
 */

/**
 * Make this into the one-stop shop of player state data.
 *
 * I have previously quite often done data by having different components and small components, I think that is actually
 * not the best idea anymore, but we learn as we go. So, Components can be slightly fatter, but still just data, and
 * the systems can be quite small and specific, but it is the data that is truly hard to keep track of. Having it in
 * the same place makes at least some things easier.
 */

class PlayerControlComponent(var controlMapper: ControlMapper, val player: Player) : Component, Pool.Poolable {

    var locked = false
    var waitsForRespawn = false
    private var cooldownRemaining = 0f
    var rof: Float = 1f

    var shotsFired = 0
        private set

    val speed get() = player.speed
    val actualSpeed : Float get() {
        return speed * if(waitsForRespawn) 0f else if(aiming) GameConstants.AIMING_SPEED_FACTOR else GameConstants.NORMAL_SPEED_FACTOR
    }

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
    
    var reloadStarted
        get() = controlMapper.needsReload
        set(value) {
            controlMapper.needsReload = value
        }

    var doContextAction
        get() = controlMapper.doContextAction
        set(value) {
            controlMapper.doContextAction = value
        }


    val triggerPulled get() = controlMapper.firing
    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aiming get() = controlMapper.aiming
    val aimVector get() = controlMapper.aimVector

    val directionVector: Vector2
        get() {
            return if (aiming) aimVector else walkVector
        }

    val compassDirection get() = aimVector.compassDirection()
    val mousePosition get() = controlMapper.mousePosition
    var latestHitPoint = vec2(0f, 0f)
    val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust).nor()
    val turning: Float
        get() {
            return controlMapper.turning
        }
    val thrust: Float
        get() {
            return controlMapper.thrust
        }
    val moving get() = walkVector.len2() != 0f

    val isInBuildMode get() = controlMapper.isInBuildMode
    val buildIfPossible get() = controlMapper.buildIfPossible

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

    var lockedInputSequence = mutableListOf<Int>()
    var checkForSequence = false
    var complexActionStatus: ComplexActionResult = ComplexActionResult.Failure
    fun requireSequence(inputSequence: List<Int>) {
        complexActionStatus = ComplexActionResult.Running
        lockedInputSequence = inputSequence.toMutableList()
        checkForSequence = true
        controlMapper.requireSequencePress = true
        controlMapper.keyPressedCallback = ::checkSequence
    }

    fun checkSequence(keyPressed: Int) {
        if (lockedInputSequence.first() == keyPressed) {
            lockedInputSequence.removeFirst()
            if (lockedInputSequence.isEmpty()) {
                complexActionStatus = ComplexActionResult.Success
                checkForSequence = false
                controlMapper.requireSequencePress = false
                controlMapper.keyPressedCallback = {}
            }
        } else {
            checkForSequence = false
            controlMapper.requireSequencePress = false
            controlMapper.keyPressedCallback = {}
            lockedInputSequence.clear()
            complexActionStatus = ComplexActionResult.Failure
        }
    }

    fun sequencePressingProgress(): ComplexActionResult {
        return complexActionStatus
    }

    var stunned = false
    val coolDowns = mutableMapOf<KMutableProperty<Boolean>, Float>()

    fun startCooldown(property: KMutableProperty<Boolean>, cooldown: Float) {
        property.setter.call(true)
        coolDowns[property] = cooldown
    }

    fun cooldownPropertyCheckIfDone(property: KMutableProperty<Boolean>, delta: Float): Boolean {
        if (!coolDowns.containsKey(property))
            return true
        coolDowns[property] = coolDowns[property]!! - delta
        if (coolDowns[property]!! <= 0f) {
            property.setter.call(false)
            coolDowns.remove(property)
            return true
        }
        return false
    }

    private val soundsThatCanPlayAndSuch = mutableMapOf<String, Boolean>()
    fun canPlay(soundGroup: String): Boolean {
        if (!soundsThatCanPlayAndSuch.containsKey(soundGroup)) {
            soundsThatCanPlayAndSuch[soundGroup] = true
        }
        return soundsThatCanPlayAndSuch[soundGroup]!!
    }

    fun hasPlayed(soundGroup: String) {
        soundsThatCanPlayAndSuch[soundGroup] = false
    }

    fun resetSound(soundGroup: String) {
        soundsThatCanPlayAndSuch[soundGroup] = true
    }

    companion object {
        val mapper = mapperFor<PlayerControlComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity):PlayerControlComponent {
            return mapper.get(entity)
        }
    }
}

fun Vector2.compassDirection(): CompassDirection {
    return when (this.angleDeg()) {
        in 248f..293f -> CompassDirection.North
        in 293f..338f -> CompassDirection.NorthWest
        in 338f..360f -> CompassDirection.West
        in 0f..23f -> CompassDirection.West
        in 23f..68f -> CompassDirection.SouthWest
        in 68f..113f -> CompassDirection.South
        in 113f..158f -> CompassDirection.SouthEast
        in 158f..203f -> CompassDirection.East
        in 203f..248f -> CompassDirection.NorthEast
        else -> CompassDirection.South
    }
}

