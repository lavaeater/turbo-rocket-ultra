package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import data.Player
import ktx.math.random
import ktx.math.vec2
import kotlin.reflect.KMutableProperty

class EnemyComponent : Component, Pool.Poolable {
    var flock = true
    var lastShotAngle = 0f
    var rushSpeed = 15f
    var fieldOfView = 180f
    var viewDistance = 90f
    var speed = 5f
    var stunned = false

    val directionVector = vec2()
    var health = 100f
    lateinit var lastHitBy: Player

    val isDead get() = health <= 0f

    var timeRemaining = 0f
    private set

    //PathFinding, useful everywhere
    var nextPosition = vec2()
    val path = Queue<Vector2>()
    var needsNewNextPosition = true

    //Unique short Id
    var id = UniqueId.next()

    fun takeDamage(damage: Float, player: Player) {
        health -= damage
        lastHitBy = player
    }

    fun takeDamage(range: ClosedFloatingPointRange<Float>, player: Player) {
        health -= range.random()
        lastHitBy = player
    }

    val coolDowns = mutableMapOf<KMutableProperty<Boolean>, Float>()

    fun startCooldown(property: KMutableProperty<Boolean>, cooldown: Float) {
        property.setter.call(true)
        coolDowns[property] = cooldown
    }

    fun cooldownPropertyCheckIfDone(property: KMutableProperty<Boolean>, delta: Float) : Boolean {
        if(!coolDowns.containsKey(property))
            return true
        coolDowns[property] = coolDowns[property]!! - delta
        if(coolDowns[property]!! <= 0f) {
            property.setter.call(false)
            coolDowns.remove(property)
            return true
        }
        return false
    }

    fun coolDown(deltaTime: Float){
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        id = UniqueId.next()
        flock = true
        nextPosition.setZero()
        path.clear()
        needsNewNextPosition = true
        fieldOfView = 90f
        speed = 2.5f
        viewDistance = 30f
        directionVector.set(Vector2.Zero)
        val randomValue = (1..100).random()
        health = if(randomValue < 5) 1000f else 100f
        timeRemaining = 0f
    }
}