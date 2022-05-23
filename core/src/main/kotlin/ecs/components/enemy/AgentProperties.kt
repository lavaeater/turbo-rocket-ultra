package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ecs.systems.graphics.GameConstants
import ktx.math.random
import ktx.math.vec2
import kotlin.reflect.KMutableProperty

class Enemy : Component, Pool.Poolable {
    override fun reset() {
    }
}

class AttackableProperties: Component, Pool.Poolable {
    var stunned = false
    var health = GameConstants.BASE_HEALTH
    lateinit var lastHitBy: Entity

    val isDead get() = health <= 0f
    fun takeDamage(damage: Float, entity: Entity) {
        health -= damage
        lastHitBy = entity
    }

    fun takeDamage(range: ClosedFloatingPointRange<Float>, entity:Entity) {
        health -= range.random()
        lastHitBy = entity
    }
    override fun reset() {
        val randomValue = (1..100).random()
        health = if (randomValue < 5) 1000f else 100f
        stunned = false
    }

}

class AgentProperties : Component, Pool.Poolable {
    var meleeDistance = GameConstants.ENEMY_MELEE_DISTANCE
    var rotationSpeed = GameConstants.ENEMY_ROTATION_SPEED //degrees per second
    var rushSpeed = GameConstants.ENEMY_RUSH_SPEED
    var flock = true
    var lastShotAngle = 0f
    var fieldOfView = 180f
    var viewDistance = 90f
    var speed = GameConstants.ENEMY_BASE_SPEED
    var baseSpeed = GameConstants.ENEMY_BASE_SPEED


    val directionVector = Vector2.X.cpy()


    var timeRemaining = 0f
        private set

    //PathFinding, useful everywhere
    var nextPosition = vec2()
    val path = Queue<Vector2>()
    var needsNewNextPosition = true

    //Unique short Id
    var id = UniqueId.next()



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

    fun coolDown(deltaTime: Float) {
        timeRemaining -= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        id = UniqueId.next()
        flock = true
        nextPosition.setZero()
        path.clear()
        needsNewNextPosition = true
        fieldOfView = GameConstants.ENEMY_FOV
        viewDistance = GameConstants.ENEMY_VIEW_DISTANCE
        directionVector.set(Vector2.X)
        timeRemaining = 0f
        rotationSpeed = GameConstants.ENEMY_ROTATION_SPEED
        speed = GameConstants.ENEMY_BASE_SPEED
        baseSpeed = GameConstants.ENEMY_BASE_SPEED
        rushSpeed = GameConstants.ENEMY_RUSH_SPEED
    }
}