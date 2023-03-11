package common.physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import eater.core.engine
import eater.ecs.ashley.components.AgentProperties
import eater.ecs.ashley.components.Box2d
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.times
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object AshleyMapperStore {
    inline fun <reified T : Component> getMapper(): ComponentMapper<T> {
        val type = typeOf<T>()
        if (!mappers.containsKey(type))
            mappers[type] = mapperFor<T>()
        return mappers[type] as ComponentMapper<T>
    }
    val mappers = mutableMapOf<KType, ComponentMapper<*>>()
}

fun Body.rightNormal(): Vector2 {
    return this.getWorldVector(Vector2.X)
}

fun Body.lateralVelocity(): Vector2 {
    val rightNormal = this.rightNormal()
    return rightNormal * this.linearVelocity.dot(rightNormal)
}

fun Body.forwardNormal(): Vector2 {
    return this.getWorldVector(Vector2.Y)
}

fun Body.forwardVelocity(): Vector2 {
    val forwardNormal = this.forwardNormal()
    return forwardNormal * this.linearVelocity.dot(forwardNormal)
}

fun Entity.body(): Body {
    return getComponent<Box2d>().body
}


fun Contact.eitherIsEntity(): Boolean {
    return this.fixtureA.body.userData is Entity || this.fixtureB.body.userData is Entity
}

fun Contact.bothAreEntities(): Boolean {
    return this.fixtureA.body.userData is Entity && this.fixtureB.body.userData is Entity
}

fun Body.isEnemy(): Boolean {
    return (userData as Entity).has<AgentProperties>()
}


fun Fixture.isEntity(): Boolean {
    return this.body.userData is Entity
}

fun Fixture.getEntity(): Entity {
    return this.body.userData as Entity
}

inline fun <reified T : Component> Entity.has(): Boolean {
    return AshleyMapperStore.getMapper<T>().has(this)
}

inline fun <reified T : Component> Entity.getComponent(): T {
    return AshleyMapperStore.getMapper<T>().get(this)
}

fun Contact.noSensors() : Boolean {
    return !this.fixtureA.isSensor && !this.fixtureB.isSensor
}

inline fun <reified T : Component> Contact.atLeastOneHas(): Boolean {
    if (this.eitherIsEntity()) {
        return if (this.fixtureA.isEntity() && this.fixtureA.getEntity()
                .has<T>()
        ) true else this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    }
    return false
}

inline fun <reified T : Component> Contact.justOneHas(): Boolean {
        val fOne = this.fixtureA.isEntity() && this.fixtureA.getEntity()
            .has<T>()
    val fTwo = this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    return fOne xor fTwo
}

inline fun<reified T: Component> getEntityThatHas(entityOne: Entity, entityTwo: Entity): Entity {
    return if(entityOne.has<T>()) entityOne else entityTwo
}
inline fun <reified T : Component> Contact.bothHaveComponent(): Boolean {
    if (this.bothAreEntities()) {
        val mapper = AshleyMapperStore.getMapper<T>()
        return this.fixtureA.getEntity().has(mapper) &&
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

fun Contact.getOtherEntity(entity: Entity): Entity {
    val entityA = this.fixtureA.getEntity()
    return if (entityA == entity) this.fixtureB.getEntity() else entityA
}

inline fun <reified T : Component> Contact.getEntityFor(): Entity {
    return if(this.fixtureA.isEntity() && this.fixtureA.getEntity().has<T>()) this.fixtureA.getEntity() else this.fixtureB.getEntity()
}

fun Contact.eitherIsSensor() : Boolean {
    return this.fixtureA.isSensor || this.fixtureB.isSensor
}

fun Contact.bothAreSensors() : Boolean {
    return this.fixtureA.isSensor && this.fixtureB.isSensor
}

fun Float.toDegrees(): Float {
    return this * MathUtils.radiansToDegrees
}

fun Float.to360Degrees(): Float {
    var rotation = this.toDegrees() % 360
    if (rotation < 0f)
        rotation += 360f
    if (rotation > 360f)
        rotation -= 360f
    return rotation
}

fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    rotation: Float = 180f
) {
    draw(
        textureRegion,
        x,
        y,
        0f,
        0f,
        textureRegion.regionWidth.toFloat(),
        textureRegion.regionHeight.toFloat(),
        scaleX,
        scaleY,
        rotation
    )
}

fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scale: Float = 1f,
    rotation: Float = 180f
) {

    drawScaled(textureRegion, x, y, scale, scale, rotation)

}

inline fun <reified T : Component> Entity.addComponent(block: T.() -> Unit = {}): T {
    val c = component(block)
    this.add(c)
    return c
}

inline fun <reified T : Component> component(block: T.() -> Unit = {}): T {
    return engine().createComponent(block)
}

inline fun <reified T : Component> Engine.createComponent(block: T.() -> Unit = {}): T {
    return this.createComponent(T::class.java).apply(block)
}
