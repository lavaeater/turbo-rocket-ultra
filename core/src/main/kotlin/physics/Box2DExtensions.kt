package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.*
import ecs.components.enemy.EnemyComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerControlComponent
import factories.engine
import gamestate.Player
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.math.times


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
    return AshleyMappers.bodyMapper.get(this).body
}

fun Entity.vehicleControlComponent() : VehicleControlComponent {
    return AshleyMappers.vehicleControlMapper.get(this)
}

fun Entity.playerControlComponent() : PlayerControlComponent {
    return AshleyMappers.playerControlMapper.get(this)
}

fun Entity.vehicle() : VehicleComponent {
    return AshleyMappers.vehicleMapper.get(this)
}

fun Contact.isEntityContact(): Boolean {
    return this.fixtureA.body.userData is Entity && this.fixtureB.body.userData is Entity
}

@ExperimentalStdlibApi
fun Body.isEnemy() : Boolean {
    return (userData as Entity).hasComponent<EnemyComponent>()
}

@ExperimentalStdlibApi
fun Body.isPlayer() : Boolean {
    return (userData as Entity).hasComponent<PlayerComponent>()
}

@ExperimentalStdlibApi
fun Body.playerComponent() : PlayerComponent {
    return (userData as Entity).getComponent()
}

@ExperimentalStdlibApi
fun Body.player() : Player {
    return (userData as Entity).getComponent<PlayerComponent>().player
}



fun Fixture.isEntity() : Boolean {
    return this.body.userData is Entity
}

fun Fixture.getEntity() : Entity {
    return this.body.userData as Entity
}

@ExperimentalStdlibApi
inline fun <reified T: Component>Entity.hasComponent() : Boolean {
    return AshleyMappers.getMapper<T>().has(this)
}

@ExperimentalStdlibApi
inline fun <reified T: Component>Entity.getComponent() : T {
    return AshleyMappers.getMapper<T>().get(this)
}

inline fun <reified T: Component>Contact.hasComponent():Boolean {
    if(this.isEntityContact()) {
        val mapper = mapperFor<T>()
        return this.fixtureA.getEntity().has(mapper) ||
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

inline fun <reified T: Component>Contact.bothHaveComponent(): Boolean {
    if(this.isEntityContact()) {
        val mapper = mapperFor<T>()
        return this.fixtureA.getEntity().has(mapper) &&
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

@ExperimentalStdlibApi
fun Contact.getPlayerFor(): Player {
    return this.getEntityFor<PlayerComponent>().getComponent<PlayerComponent>().player
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
    var rotation = this.toDegrees() % 360
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


inline fun<reified T: Component>Entity.addComponent(block: T.() -> Unit = {}) {
    this.add(component(block))
}

inline fun<reified T: Component>component(block: T.()-> Unit = {}) :T {
    return engine().createComponent(block)
}

inline fun<reified T: Component> Engine.createComponent(block: T.() -> Unit = {}): T {
    return this.createComponent(T::class.java).apply(block)
}