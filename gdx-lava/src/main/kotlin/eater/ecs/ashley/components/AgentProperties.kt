package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ktx.ashley.mapperFor
import ktx.math.vec2
import kotlin.reflect.KMutableProperty

class AgentProperties(val baseProperties: AgentBaseProperties) : Component, Pool.Poolable {
    constructor() : this(AgentBaseProperties.reusableBaseProperties)

    var meleeDamageRange = baseProperties.meleeDamageRange
    var attackSpeed = baseProperties.attackSpeed
    var meleeDistance = baseProperties.meleeDistance
    var rotationSpeed = baseProperties.rotationSpeed //degrees per second
    var rushSpeed = baseProperties.rushSpeed
    var speed = baseProperties.speed
    var flock = baseProperties.flock
    var fieldOfView = baseProperties.fieldOfView
    var viewDistance = baseProperties.viewDistance
    var lastShotAngle = 0f

    val directionVector = Vector2.X.cpy()!!


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
        fieldOfView = baseProperties.fieldOfView
        viewDistance = baseProperties.fieldOfView
        directionVector.set(Vector2.X)
        timeRemaining = 0f
        rotationSpeed = baseProperties.rotationSpeed
        speed = baseProperties.speed
        rushSpeed = baseProperties.rushSpeed
    }

    companion object {
        val mapper = mapperFor<AgentProperties>()
        fun get(entity: Entity): AgentProperties {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}