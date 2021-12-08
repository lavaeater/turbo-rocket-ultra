package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ecs.components.BodyComponent
import ecs.components.ai.*
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySensorComponent
import ecs.components.enemy.EnemySpawnerComponent
import ecs.components.enemy.TackleComponent
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.*
import ecs.components.graphics.*
import ecs.components.pickups.LootComponent
import ecs.components.pickups.LootDropComponent
import ecs.components.player.*
import ktx.ashley.mapperFor
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object AshleyMappers {
    @kotlin.ExperimentalStdlibApi
    inline fun <reified T : Component> getMapper(): ComponentMapper<T> {
        val type = typeOf<T>()
        if (!mappers.containsKey(type))
            mappers[type] = mapperFor<T>()
        return mappers[type] as ComponentMapper<T>
    }

    val hacking = mapperFor<HackingComponent>()
    val destroyAfterReading = mapperFor<DestroyAfterCoolDownComponent>()
    val body = mapperFor<BodyComponent>()
    val mappers = mutableMapOf<KType, ComponentMapper<*>>()
    val transform = mapperFor<TransformComponent>()
    val amble = mapperFor<Amble>()
    val attackPlayer = mapperFor<AttackPlayer>()
    val behavior = mapperFor<BehaviorComponent>()
    val chasePlayer = mapperFor<ChasePlayer>()
    val gib = mapperFor<GibComponent>()
    val investigate = mapperFor<Investigate>()
    val noticedSomething = mapperFor<NoticedSomething>()
    val playerIsInRange = mapperFor<PlayerIsInRange>()
    val seekPlayer = mapperFor<SeekPlayer>()
    val trackingPlayer = mapperFor<TrackingPlayer>()
    val enemy = mapperFor<EnemyComponent>()
    val enemySensor = mapperFor<EnemySensorComponent>()
    val enemySpawner = mapperFor<EnemySpawnerComponent>()
    val tackle = mapperFor<TackleComponent>()
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
    val frustum = mapperFor<OnScreenComponent>()
    val sprite = mapperFor<SpriteComponent>()
    val anchors = mapperFor<AnchorPointsComponent>()
    val build = mapperFor<BuildComponent>()
    val complexAction = mapperFor<ComplexActionComponent>()
}

fun Entity.transform(): TransformComponent {
    return AshleyMappers.transform.get(this)
}

fun Entity.hasTransform(): Boolean {
    return AshleyMappers.transform.has(this)
}

fun Entity.build(): BuildComponent {
    return AshleyMappers.build.get(this)
}

fun Entity.hasBuild(): Boolean {
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

fun Entity.sprite(): SpriteComponent {
    return AshleyMappers.sprite.get(this)
}

fun Entity.hasSprite(): Boolean {
    return AshleyMappers.sprite.has(this)
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

fun Entity.hasPlayerControl(): Boolean {
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

fun Entity.hasObjective(): Boolean {
    return AshleyMappers.objective.has(this)
}

fun Entity.hacking(): HackingComponent {
    return AshleyMappers.hacking.get(this)
}

fun Entity.hasHacking(): Boolean {
    return AshleyMappers.hacking.has(this)
}