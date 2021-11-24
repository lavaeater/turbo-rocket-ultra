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
import data.Player
import ktx.ashley.has
import ktx.math.times


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

@OptIn(ExperimentalStdlibApi::class)
fun Entity.body(): Body {
    return getComponent<BodyComponent>().body!!
}

@OptIn(ExperimentalStdlibApi::class)
fun Entity.vehicleControlComponent(): VehicleControlComponent {
    return this.getComponent()
}

@OptIn(ExperimentalStdlibApi::class)
fun Entity.playerControlComponent(): PlayerControlComponent {
    return this.getComponent()
}

@OptIn(ExperimentalStdlibApi::class)
fun Entity.vehicle(): VehicleComponent {
    return this.getComponent()
}

fun Contact.eitherIsEntity(): Boolean {
    return this.fixtureA.body.userData is Entity || this.fixtureB.body.userData is Entity
}

fun Contact.bothAreEntities(): Boolean {
    return this.fixtureA.body.userData is Entity && this.fixtureB.body.userData is Entity
}

@ExperimentalStdlibApi
fun Body.isEnemy(): Boolean {
    return (userData as Entity).has<EnemyComponent>()
}

@ExperimentalStdlibApi
fun Body.isPlayer(): Boolean {
    return (userData as Entity).has<PlayerComponent>()
}

@ExperimentalStdlibApi
fun Body.playerComponent(): PlayerComponent {
    return (userData as Entity).getComponent()
}

@ExperimentalStdlibApi
fun Body.player(): Player {
    return (userData as Entity).getComponent<PlayerComponent>().player
}

@OptIn(ExperimentalStdlibApi::class)
fun Fixture.isPlayer(): Boolean {
    return this.body.userData is Entity && (this.body.isPlayer())
}

fun Fixture.isEntity(): Boolean {
    return this.body.userData is Entity
}

fun Fixture.getEntity(): Entity {
    return this.body.userData as Entity
}

@ExperimentalStdlibApi
inline fun <reified T : Component> Entity.has(): Boolean {
    return AshleyMappers.getMapper<T>().has(this)
}

@ExperimentalStdlibApi
inline fun <reified T : Component> Entity.getComponent(): T {
    return AshleyMappers.getMapper<T>().get(this)
}

fun Contact.noSensors() : Boolean {
    return !this.fixtureA.isSensor && !this.fixtureB.isSensor()
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Component> Contact.atLeastOneHas(): Boolean {
    if (this.eitherIsEntity()) {
        return if (this.fixtureA.isEntity() && this.fixtureA.getEntity()
                .has<T>()
        ) true else this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    }
    return false
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Component> Contact.justOneHas(): Boolean {

        val fOne = this.fixtureA.isEntity() && this.fixtureA.getEntity()
            .has<T>()
    val fTwo = this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    return fOne xor fTwo
}



@ExperimentalStdlibApi
inline fun <reified T: Component> doesEitherOneHave(entityOne: Entity, entityTwo: Entity): Boolean {
    return entityOne.has<T>() || entityTwo.has<T>()
}

@ExperimentalStdlibApi
inline fun<reified T: Component> getEntityThatHas(entityOne: Entity, entityTwo: Entity): Entity {
    return if(entityOne.has<T>()) entityOne else entityTwo
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Component> Contact.bothHaveComponent(): Boolean {
    if (this.bothAreEntities()) {
        val mapper = AshleyMappers.getMapper<T>()
        return this.fixtureA.getEntity().has(mapper) &&
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

@ExperimentalStdlibApi
fun Contact.getPlayerFor(): Player {
    return this.getEntityFor<PlayerComponent>().getComponent<PlayerComponent>().player
}

fun Contact.getOtherEntity(entity: Entity): Entity {
    val entityA = this.fixtureA.getEntity()
    return if (entityA == entity) this.fixtureB.getEntity() else entityA
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Component> Contact.getEntityFor(): Entity {
    return if(this.fixtureA.isEntity() && this.fixtureA.getEntity().has<T>()) this.fixtureA.getEntity() else this.fixtureB.getEntity()
}

fun Contact.isPlayerByPlayerContact(): Boolean {
    return this.bothAreEntities() && this.bothHaveComponent<PlayerComponent>()
}

fun Contact.eitherIsSensor() : Boolean {
    return this.fixtureA.isSensor || this.fixtureB.isSensor
}

fun Contact.isPlayerContact(): Boolean {
    if (this.bothAreEntities()) {
        return this.atLeastOneHas<PlayerComponent>()
    }
    return false
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