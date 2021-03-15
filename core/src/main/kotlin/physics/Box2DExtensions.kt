package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.*
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.times
import kotlin.reflect.KType
import kotlin.reflect.typeOf


object Mappers {
//    @kotlin.ExperimentalStdlibApi
//    inline fun <reified T:Component>getMapper(): ComponentMapper<T> {
//        val type = typeOf<T>()
//        if(!mappers.containsKey(type))
//            mappers[type] = mapperFor<T>()
//        return mappers[type] as ComponentMapper<T>
//    }

    val transformMapper = mapperFor<TransformComponent>()
    val characterSpriteComponentMapper = mapperFor<CharacterSpriteComponent>()
    val playerControlMapper = mapperFor<PlayerControlComponent>()
    val bodyMapper = mapperFor<BodyComponent>()
    val vehicleMapper = mapperFor<VehicleComponent>()
    val vehicleControlMapper = mapperFor<VehicleControlComponent>()
}


fun Body.rightNormal() : Vector2 {
    return this.getWorldVector(Vector2.X)
}

fun Body.lateralVelocity() : Vector2 {
    val rightNormal = this.rightNormal()
    return rightNormal * this.linearVelocity.dot(rightNormal)
}

fun Body.forwardNormal(): Vector2 {
    return this.getWorldVector(Vector2.Y)
}

fun Body.forwardVelocity() : Vector2 {
    val forwardNormal = this.forwardNormal()
    return forwardNormal * this.linearVelocity.dot(forwardNormal)
}

fun Entity.body() : Body {
    return Mappers.bodyMapper.get(this).body
}

fun Entity.vehicleControlComponent() : VehicleControlComponent {
    return Mappers.vehicleControlMapper.get(this)
}

fun Entity.playerControlComponent() : PlayerControlComponent {
    return Mappers.playerControlMapper.get(this)
}

fun Entity.vehicle() : VehicleComponent {
    return Mappers.vehicleMapper.get(this)
}

fun Contact.isEntityContact(): Boolean {
    return this.fixtureA.body.userData is Entity && this.fixtureB.body.userData is Entity
}

fun Fixture.getEntity() : Entity {
    return this.body.userData as Entity
}

//@ExperimentalStdlibApi
//inline fun <reified T: Component>Entity.hasComponent() : Boolean {
//    return Mappers.getMapper<T>().has(this)
//}

//@ExperimentalStdlibApi
//inline fun <reified T: Component>Entity.getComponent() : T {
//    return Mappers.getMapper<T>().get(this)
//}




inline fun <reified T: Component>Contact.hasComponent():Boolean {
    if(this.isEntityContact()) {
        val mapper = mapperFor<T>()
        return this.fixtureA.getEntity().has(mapper) ||
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

inline fun <reified T:Component> Contact.getEntityFor(): Entity {
    val mapper = mapperFor<T>()
    val entityA = this.fixtureA.getEntity()
    val entityB = this.fixtureB.getEntity()
    return if(entityA.has(mapper)) entityA else entityB
}

fun Contact.isPlayerContact(): Boolean {
    if(this.isEntityContact()) {
        return this.hasComponent<PlayerComponent>()
    }
    return false
}

fun Float.toDegrees() : Float {
    return this * MathUtils.radiansToDegrees
}

fun Float.to360Degrees() : Float {
    var rotation = this * MathUtils.radiansToDegrees
    if(rotation < 0f)
        rotation += 360f
    if(rotation > 360f)
        rotation -= 360f
    return rotation
}

fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scale: Float = 1f,
    rotation: Float = 180f) {

    draw(
        textureRegion,
        x,
        y,
        0f,
        0f,
        textureRegion.regionWidth.toFloat(),
        textureRegion.regionHeight.toFloat(),
        scale,
        scale,
        rotation)
}