package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
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
    inline fun <reified T: Component>getMapper(): ComponentMapper<T> {
        val type = typeOf<T>()
        if(!mappers.containsKey(type))
            mappers[type] = mapperFor<T>()
        return mappers[type] as ComponentMapper<T>
    }

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
    val texture = mapperFor<TextureComponent>()
    val loot = mapperFor<LootComponent>()
    val lootDrop = mapperFor<LootDropComponent>()
    val contextAction = mapperFor<ContextActionComponent>()
    val firedShots = mapperFor<FiredShotsComponent>()
    val playerControl = mapperFor<PlayerControlComponent>()
    val weapon = mapperFor<WeaponComponent>()
    val respawn = mapperFor<PlayerIsRespawning>()
    val waitsForRespawn = mapperFor<PlayerWaitsForRespawn>()
    val frustum = mapperFor<InFrustumComponent>()
    val sprite = mapperFor<SpriteComponent>()

}