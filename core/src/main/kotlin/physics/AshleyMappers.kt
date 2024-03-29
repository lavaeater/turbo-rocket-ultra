package physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import eater.ecs.ashley.components.AgentProperties
import eater.ecs.ashley.components.Box2d
import eater.ecs.ashley.components.TransformComponent
import eater.physics.addComponent
import ecs.components.AudioComponent
import ecs.components.ai.BehaviorComponent
import ecs.components.ai.CollidedWithObstacle
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.*
import ecs.components.fx.GibComponent
import ecs.components.fx.ParticleEffectComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.*
import ecs.components.intent.*
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ktx.ashley.mapperFor
import tru.Assets
import tru.getRandomSoundFor
import ui.UiThingComponent

object AshleyMappers {
    val audio = mapperFor<AudioComponent>()
    val hacking = mapperFor<HackingComponent>()
    val destroyAfterCooldown = mapperFor<DestroyAfterCoolDownComponent>()
    val body = mapperFor<Box2d>()
    val transform = mapperFor<TransformComponent>()
    val perimeter = mapperFor<PerimeterObjectiveComponent>()
    val behavior = mapperFor<BehaviorComponent>()
    val gib = mapperFor<GibComponent>()
    val noticedSomething = mapperFor<NoticedSomething>()
    val agentProps = mapperFor<AgentProperties>()
    val enemy = mapperFor<Enemy>()
    val enemySensor = mapperFor<EnemySensorComponent>()
    val enemySpawner = mapperFor<EnemySpawnerComponent>()
    val splatter = mapperFor<SplatterComponent>()
    val aim = mapperFor<AimComponent>()
    val bullet = mapperFor<BulletComponent>()
    val destroy = mapperFor<DestroyComponent>()
    val objective = mapperFor<ObjectiveComponent>()
    val obstacle = mapperFor<ObstacleComponent>()
    val shot = mapperFor<ShotComponent>()
    val animatedCharacter = mapperFor<AnimatedCharacterComponent>()
    val cameraFollow = mapperFor<CameraFollowComponent>()
    val miniMap = mapperFor<MiniMapComponent>()
    val loot = mapperFor<LootComponent>()
    val lootDrop = mapperFor<LootDropComponent>()
    val contextAction = mapperFor<ContextActionComponent>()
    val firedShots = mapperFor<FiredShotsComponent>()
    val playerControl = mapperFor<PlayerControlComponent>()
    val weapon = mapperFor<WeaponComponent>()
    val respawn = mapperFor<PlayerIsRespawning>()
    val waitsForRespawn = mapperFor<PlayerWaitsForRespawn>()
    val textureRegion = mapperFor<TextureRegionComponent>()
    val renderable = mapperFor<RenderableComponent>()
    val anchors = mapperFor<AnchorPointsComponent>()
    val build = mapperFor<BuildModeComponent>()
    val complexAction = mapperFor<ComplexActionComponent>()
    val light = mapperFor<LightComponent>()
    val intent = mapperFor<IntentComponent>()
    val calculatedPosition = mapperFor<CalculatedPositionComponent>()
    val calculatedRotation = mapperFor<CalculatedRotationComponent>()
    val functions = mapperFor<FunctionsComponent>()
    val obstacleCollision = mapperFor<CollidedWithObstacle>()
    val uiThing = mapperFor<UiThingComponent>()
    val effect = mapperFor<ParticleEffectComponent>()
    val weaponEntity = mapperFor<WeaponEntityComponent>()
    val inventory = mapperFor<InventoryComponent>()
    val reloading = mapperFor<IsReloadingComponent>()
    val fitness = mapperFor<Fitness>()
    val attackableProperties = mapperFor<AttackableProperties>()
}

fun Entity.attackables(): AttackableProperties {
    return AshleyMappers.attackableProperties.get(this)
}

fun Entity.hasBody(): Boolean {
    return AshleyMappers.body.has(this)
}

fun Entity.fitnesScore() : Int {
    return AshleyMappers.fitness.get(this).fitness
}
fun Entity.fitnessUp() {
    AshleyMappers.fitness.get(this).fitness++
}

fun Entity.fitnessDown() {
    AshleyMappers.fitness.get(this).fitness--
}

fun Entity.isReloading() : Boolean {
    return AshleyMappers.reloading.has(this)
}

fun Entity.startReloading() {
    this.addComponent<IsReloadingComponent> {  }
}

fun Entity.reloader() : IsReloadingComponent {
    return AshleyMappers.reloading.get(this)
}

fun Entity.inventory(): InventoryComponent {
    return AshleyMappers.inventory.get(this)
}

fun Entity.hasWeaponEntity() : Boolean {
    return AshleyMappers.weaponEntity.has(this)
}

fun Entity.weaponEntity(): Entity {
    return AshleyMappers.weaponEntity.get(this).weaponEntity
}


fun Entity.hasSplatter(): Boolean {
    return AshleyMappers.splatter.has(this)
}

fun Entity.splatterEffect() : SplatterComponent {
    return AshleyMappers.splatter.get(this)
}

fun Entity.hasEffect(): Boolean {
    return AshleyMappers.effect.has(this)
}

fun Entity.effect() : ParticleEffectComponent {
    return AshleyMappers.effect.get(this)
}

fun Entity.bullet(): BulletComponent {
    return AshleyMappers.bullet.get(this)
}

fun Entity.uiThing(): UiThingComponent {
    return AshleyMappers.uiThing.get(this)
}
fun Entity.hasUiThing() : Boolean {
    return AshleyMappers.uiThing.has(this)
}

fun Entity.hasCollidedWithObstacle() : Boolean {
    return AshleyMappers.obstacleCollision.has(this)
}

fun Entity.getCalculatedPosition(): Vector2 {
    return AshleyMappers.calculatedPosition.get(this).calculate()
}

fun Entity.getCalculatedRotation(): Float {
    return AshleyMappers.calculatedRotation.get(this).calculate()
}

fun Entity.runFunctions() {
    for(f in AshleyMappers.functions.get(this).functions.values) f(this)
}

fun Entity.runFunction(key: String) {
    AshleyMappers.functions.get(this).functions[key]!!(this)
}

fun Entity.intendsTo(intendsTo: IntendsTo): Boolean {
    return this.intent() == intendsTo
}

fun Entity.hasIntent(): Boolean {
    return AshleyMappers.intent.has(this)
}

fun Entity.intent(): IntendsTo {
    return AshleyMappers.intent.get(this).intendsTo
}

fun Entity.intendTo(intent: IntendsTo) {
    this.addComponent<IntentComponent> {
        intendsTo = intent
    }
}

fun Entity.audio(): AudioComponent {
    return AshleyMappers.audio.get(this)
}

fun Entity.hasAudio() : Boolean {
    return AshleyMappers.audio.has(this)
}

fun Entity.playRandomAudioFor(category:String, subCategory:String, once: Boolean = true) {
    if (this.hasAudio()) {
        val audio = this.audio()
        audio.playSound(Assets.newSoundEffects.getRandomSoundFor("zombies", "groans"), once)
        audio.coolDownRange = 60f..120f
    }
}

fun Entity.agentProps(): AgentProperties {
    return AshleyMappers.agentProps.get(this)
}

fun Entity.isEnemy(): Boolean {
    return AshleyMappers.enemy.has(this)
}

fun Entity.transform(): TransformComponent {
    return AshleyMappers.transform.get(this)
}

fun Entity.hasTransform(): Boolean {
    return AshleyMappers.transform.has(this)
}

fun Entity.perimeter() : PerimeterObjectiveComponent {
    return AshleyMappers.perimeter.get(this)
}

fun Entity.buildModal(): BuildModeComponent {
    return AshleyMappers.build.get(this)
}

fun Entity.inBuildMode(): Boolean {
    return AshleyMappers.build.has(this)
}

fun Entity.anchors(): AnchorPointsComponent {
    return AshleyMappers.anchors.get(this)
}

fun Entity.hasAnchors(): Boolean {
    return AshleyMappers.anchors.has(this)
}

fun Entity.behavior(): BehaviorComponent {
    return AshleyMappers.behavior.get(this)
}

fun Entity.hasBehavior(): Boolean {
    return AshleyMappers.behavior.has(this)
}

fun Entity.textureRegionComponent(): TextureRegionComponent {
    return AshleyMappers.textureRegion.get(this)
}

fun Entity.renderable(): RenderableComponent {
    return AshleyMappers.renderable.get(this)
}

fun Entity.hasSprite(): Boolean {
    return AshleyMappers.textureRegion.has(this)
}

fun Entity.weapon(): WeaponComponent {
    return AshleyMappers.weapon.get(this)
}

fun Entity.hasWeapon(): Boolean {
    return AshleyMappers.weapon.has(this)
}

fun Entity.animation(): AnimatedCharacterComponent {
    return AshleyMappers.animatedCharacter.get(this)
}

fun Entity.hasAnimation(): Boolean {
    return AshleyMappers.animatedCharacter.has(this)
}

fun Entity.contextAction(): ContextActionComponent {
    return AshleyMappers.contextAction.get(this)
}

fun Entity.hasContextAction(): Boolean {
    return AshleyMappers.contextAction.has(this)
}

inline fun Entity.addContextAction(block: ContextActionComponent.() -> Unit = {}) {
    this.addComponent(block)
}

fun Entity.playerControl(): PlayerControlComponent {
    return AshleyMappers.playerControl.get(this)
}

fun Entity.isPlayer(): Boolean {
    return AshleyMappers.playerControl.has(this)
}

fun Entity.safeDestroy() {
    this.addComponent<DestroyComponent>()
}

fun Entity.complexAction(): ComplexActionComponent {
    return AshleyMappers.complexAction.get(this)
}

fun Entity.objective(): ObjectiveComponent {
    return AshleyMappers.objective.get(this)
}

fun Entity.light(): LightComponent {
    return AshleyMappers.light.get(this)
}

fun Entity.hasObjective(): Boolean {
    return AshleyMappers.objective.has(this)
}

fun Entity.hacking(): HackingComponent {
    return AshleyMappers.hacking.get(this)
}

fun Entity.hasHacking(): Boolean {
    return AshleyMappers.hacking.has(this)
}

fun Entity.hasObstacle() : Boolean {
    return AshleyMappers.obstacle.has(this)
}