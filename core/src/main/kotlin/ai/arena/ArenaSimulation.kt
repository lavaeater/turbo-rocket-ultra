package ai.arena

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import components.*
import components.ai.BehaviorComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import dependencies.InjectionContext
import ktx.inject.register
import ktx.ashley.add
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.createWorld
import ktx.math.vec2
import physics.AshleyMappers
import physics.BodyEntityMapper
import physics.ContactManager
import systems.Box2dUpdateSystem
import systems.PhysicsSystem
import systems.ai.BehaviorTreeSystem
import systems.ai.DestroyAfterCooldownSystem
import systems.ai.UpdateTimePieceSystem
import systems.enemy.EnemyMovementSystem

/**
 * Self-contained headless simulation of one enemy vs one player bot.
 *
 * Uses a fresh World + PooledEngine so it does not interfere with the game world.
 * The global InjectionContext World and Engine are temporarily swapped for the
 * duration of the simulation so that behavior tree tasks resolve the arena world.
 *
 * [runs] simulations of [simDuration] seconds each are averaged into a single result.
 */
object ArenaSimulation {

    private const val FIXED_DT = 1f / 30f
    private const val ENEMY_HEALTH = 100f
    private const val PLAYER_HEALTH = 100f

    fun evaluate(
        tree: BehaviorTree<Entity>,
        simDuration: Float = 60f,
        runs: Int = 3,
        seed: Long = 42L
    ): SimulationResult {
        val results = (0 until runs).map { runIndex ->
            runSingle(tree, simDuration, seed + runIndex)
        }
        return average(results)
    }

    private fun runSingle(tree: BehaviorTree<Entity>, simDuration: Float, seed: Long): SimulationResult {
        val arenaWorld = createWorld(gravity = Vector2.Zero, allowSleep = true)
        val engine = PooledEngine()

        val savedWorld = swapInjected(arenaWorld)
        val savedEngine = swapInjectedEngine(engine)

        return try {
            runSimulation(tree, engine, arenaWorld, simDuration, seed)
        } finally {
            // Restore global singletons
            swapInjected(savedWorld)
            swapInjectedEngine(savedEngine)
            arenaWorld.dispose()
        }
    }

    private fun runSimulation(
        candidateTree: BehaviorTree<Entity>,
        engine: PooledEngine,
        world: World,
        simDuration: Float,
        seed: Long
    ): SimulationResult {
        // Build entities
        val playerEntity = makePlayer(engine, world, vec2(5f, 5f))
        val enemyEntity  = makeEnemy(engine, world, candidateTree, vec2(20f, 20f))

        // Wire systems
        engine.apply {
            addSystem(UpdateTimePieceSystem())
            addSystem(SimulatedPlayerInputSystem({ if (isAlive(enemyEntity)) enemyEntity else null }, seed))
            addSystem(BehaviorTreeSystem(1))
            addSystem(EnemyMovementSystem(false))
            addSystem(Box2dUpdateSystem(FIXED_DT, 6, 2))
            addSystem(PhysicsSystem(0))
            addSystem(DestroyAfterCooldownSystem())
        }

        // Tracking vars
        var elapsed = 0f
        val playerStartHealth = PLAYER_HEALTH
        val enemyStartHealth = ENEMY_HEALTH
        var closestApproach = Float.MAX_VALUE
        var timeToFirstContact = Float.MAX_VALUE
        var playerHealth = playerStartHealth
        var enemyHealth = enemyStartHealth

        // Step loop
        val steps = (simDuration / FIXED_DT).toInt()
        for (step in 0 until steps) {
            if (!isAlive(playerEntity) || !isAlive(enemyEntity)) break

            GdxAI.getTimepiece().update(FIXED_DT)
            engine.update(FIXED_DT)
            elapsed += FIXED_DT

            val pPos = playerEntity.transform().position
            val ePos = enemyEntity.transform().position
            val dist = pPos.dst(ePos)
            if (dist < closestApproach) closestApproach = dist

            val nowPlayerHealth = AttackableProperties.get(playerEntity).health
            val nowEnemyHealth  = AttackableProperties.get(enemyEntity).health
            if (timeToFirstContact == Float.MAX_VALUE && nowPlayerHealth < playerHealth)
                timeToFirstContact = elapsed
            playerHealth = nowPlayerHealth
            enemyHealth  = nowEnemyHealth
        }

        val damageToPlayer = (playerStartHealth - playerHealth).coerceAtLeast(0f)
        val damageToEnemy  = (enemyStartHealth  - enemyHealth ).coerceAtLeast(0f)
        val playerKilled   = !isAlive(playerEntity)

        return SimulationResult(
            enemySurvivalTime      = elapsed,
            damageDealtToPlayer    = damageToPlayer,
            damageTakenByEnemy     = damageToEnemy,
            playerKilled           = playerKilled,
            closestApproachToPlayer = closestApproach,
            timeToFirstContact     = timeToFirstContact
        )
    }

    // ──────────────────────────── entity builders ──────────────────────

    private fun makePlayer(engine: PooledEngine, world: World, at: Vector2): Entity {
        val body = world.body {
            position.set(at)
            circle(0.5f)
        }
        val entity = engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply { position.set(at) })
            add(engine.createComponent(Box2d::class.java).apply { this.body = body })
            add(engine.createComponent(AgentProperties::class.java))
            add(engine.createComponent(AttackableProperties::class.java).apply {
                health = PLAYER_HEALTH; maxHealth = PLAYER_HEALTH
            })
            add(engine.createComponent(PlayerComponent::class.java))
        }
        engine.addEntity(entity)
        return entity
    }

    private fun makeEnemy(engine: PooledEngine, world: World, tree: BehaviorTree<Entity>, at: Vector2): Entity {
        val body = world.body {
            position.set(at)
            circle(0.5f)
        }
        // Round-trip through JSON to get a fully independent copy of the tree
        val freshTree = runCatching {
            ai.behaviorTree.serialization.BehaviorTreeSerializer.deserialize(
                ai.behaviorTree.serialization.BehaviorTreeSerializer.serialize(tree)
            )
        }.getOrElse { tree }

        val entity = engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply { position.set(at) })
            add(engine.createComponent(Box2d::class.java).apply { this.body = body })
            add(engine.createComponent(AgentProperties::class.java))
            add(engine.createComponent(AttackableProperties::class.java).apply {
                health = ENEMY_HEALTH; maxHealth = ENEMY_HEALTH
            })
            add(engine.createComponent(BehaviorComponent::class.java).apply {
                this.tree = freshTree
            })
        }
        engine.addEntity(entity)
        freshTree.`object` = entity
        return entity
    }

    private fun isAlive(entity: Entity): Boolean {
        if (!AshleyMappers.attackableProperties.has(entity)) return true
        return !AttackableProperties.get(entity).isDead
    }

    // ──────────────────────────── result averaging ─────────────────────

    private fun average(results: List<SimulationResult>): SimulationResult {
        if (results.isEmpty()) return SimulationResult.EMPTY
        val n = results.size.toFloat()
        return SimulationResult(
            enemySurvivalTime      = results.sumOf { it.enemySurvivalTime.toDouble() }.toFloat() / n,
            damageDealtToPlayer    = results.sumOf { it.damageDealtToPlayer.toDouble() }.toFloat() / n,
            damageTakenByEnemy     = results.sumOf { it.damageTakenByEnemy.toDouble() }.toFloat() / n,
            playerKilled           = results.count { it.playerKilled } > results.size / 2,
            closestApproachToPlayer = results.minOf { it.closestApproachToPlayer },
            timeToFirstContact     = results.minOf { it.timeToFirstContact }
        )
    }

    // ──────────────────────────── context swapping ─────────────────────

    private fun swapInjected(newWorld: World): World {
        val old = InjectionContext.context.inject<World>()
        InjectionContext.context.remove<World>()
        InjectionContext.context.register { bindSingleton(newWorld) }
        return old
    }

    private fun swapInjectedEngine(newEngine: com.badlogic.ashley.core.Engine): com.badlogic.ashley.core.Engine {
        val old = runCatching { InjectionContext.context.inject<com.badlogic.ashley.core.Engine>() }.getOrNull()
        InjectionContext.context.remove<com.badlogic.ashley.core.Engine>()
        InjectionContext.context.register { bindSingleton(newEngine) }
        return old ?: newEngine
    }
}

// Extension to get transform safely (reads from component, not Box2D)
private fun Entity.transform(): TransformComponent = TransformComponent.get(this)
