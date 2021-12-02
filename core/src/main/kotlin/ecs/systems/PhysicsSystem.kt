package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ecs.components.BodyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import map.grid.GridMapSection
import physics.AshleyMappers

class PhysicsSystem :
    IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bodyComponent = AshleyMappers.body.get(entity)
        val bodyPosition = bodyComponent.body!!.position
        val bodyRotation = bodyComponent.body!!.angle
        val transformComponent = AshleyMappers.transform.get(entity)
        transformComponent.position.set(bodyPosition)
        transformComponent.rotation = bodyRotation
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