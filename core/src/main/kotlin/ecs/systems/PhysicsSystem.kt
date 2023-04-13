package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import eater.ecs.ashley.components.Box2d
import eater.ecs.ashley.components.TransformComponent
import eater.injection.InjectionContext.Companion.inject
import eater.physics.has
import ecs.components.gameplay.GrenadeComponent
import ecs.components.gameplay.MolotovComponent
import ktx.ashley.allOf
import ktx.math.vec2
import map.grid.GridMapSection
import physics.AshleyMappers
import physics.ContactManager
import physics.ContactType

class PhysicsSystem(priority: Int) :
    IteratingSystem(allOf(Box2d::class, TransformComponent::class).get(), priority) {

    val g = -10f //is y up or down?
    private val contactManager by lazy { inject<ContactManager>()}

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = AshleyMappers.body.get(entity)
        val bodyPosition = bodyComponent.body!!.position
        val bodyRotation = bodyComponent.body!!.angle
        val transformComponent = AshleyMappers.transform.get(entity)
        transformComponent.position.set(bodyPosition)
        transformComponent.angleRadians = bodyRotation

        if(transformComponent.feelsGravity) {
            val body = bodyComponent.body!!
            /**
             * We must first accelerate
             */


            if(transformComponent.height > 0f) {
                transformComponent.verticalSpeed += g * deltaTime
                transformComponent.height += transformComponent.verticalSpeed//Modify with delta time?

                body.applyForce(vec2(0f, -g * deltaTime), body.worldCenter, true)
            } else {
                transformComponent.verticalSpeed = 0f
                transformComponent.height = 0f
                bodyComponent.body!!.linearVelocity = vec2(bodyComponent.body!!.linearVelocity.x, 0f)
                if(entity.has<GrenadeComponent>()) {
                    contactManager.handleGrenadeHittingAnything(ContactType.GrenadeHittingAnything(entity))
                }
                if(entity.has<MolotovComponent>()) {
                    contactManager.handleMolotovHittingAnything(ContactType.MolotovHittingAnything(entity))
                }
            }
        }
    }
}

fun Vector2.tileWorldX(): Float {
    return this.tileX().tileWorldX()
}

fun Vector2.tileWorldY(): Float {
    return this.tileY().tileWorldY()
}

fun Vector2.sectionX(): Int {
    return this.tileX() / GridMapSection.width
}

fun Vector2.sectionY(): Int {
    return this.tileY() / GridMapSection.height
}

fun Vector2.tileX(): Int {
    return this.tileX(GridMapSection.scaledWidth)
}

fun Vector2.tileY(): Int {
    return this.tileY(GridMapSection.scaledHeight)
}

fun Vector2.tileX(tileWidth: Float): Int {
    return (this.x / tileWidth).toInt()
}

fun Vector2.tileY(tileHeight: Float): Int {
    return (this.y / tileHeight).toInt()
}

fun Float.tileX(tileWidth: Float = GridMapSection.tileWidth): Int {
    return (this / tileWidth).toInt()
}

fun Float.tileY(tileHeight: Float = GridMapSection.tileHeight): Int {
    return (this / tileHeight).toInt()
}

fun Int.tileWorldX(): Float {
    return this.tileWorldX(GridMapSection.scaledWidth)
}

fun Int.tileWorldX(tileWidth: Float): Float {
    return this * tileWidth
}

fun Int.tileWorldY(): Float {
    return this.tileWorldY(GridMapSection.scaledHeight)
}

fun Int.tileWorldY(tileHeight: Float): Float {
    return this * tileHeight
}