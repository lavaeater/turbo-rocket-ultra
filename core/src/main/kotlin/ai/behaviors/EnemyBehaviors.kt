package ai.behaviors

import ai.findPathFromTo
import ai.pathfinding.TileGraph
import ai.tasks.leaf.SectionFindingMethods
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import eater.ai.*
import eater.ecs.components.AgentProperties
import eater.ecs.components.Memory
import ecs.components.ai.behavior.*
import ecs.components.enemy.AttackableProperties
import ecs.components.gameplay.BurningComponent
import ecs.components.gameplay.TargetComponent
import ecs.components.player.PlayerComponent
import ecs.systems.graphics.GameConstants
import ecs.systems.graphics.GameConstants.TOUCHING_DISTANCE
import ecs.systems.sectionX
import ecs.systems.sectionY
import ktx.log.debug
import ktx.math.minus
import ktx.math.random
import physics.agentProps
import physics.attackables
import physics.transform
import kotlin.reflect.full.starProjectedType

object EnemyBehaviors {
    val panik = ConsideredActionWithState(
        "PANIK!",
        {},
        { entity, state, deltaTime ->
            when (state.status) {
                PanikStatus.NotStarted -> {
                    state.coolDown = 0f
                    state.coolDownRange = 0.5f..1.5f
                    state.status = PanikStatus.Paniking
                }
                PanikStatus.Paniking -> {
                    if (state.ready(deltaTime)) {
                        val agentProps = AgentProperties.get(entity)
                        agentProps.directionVector.rotateDeg((45f..135f).random())
                        agentProps.speed = agentProps.baseProperties.rushSpeed
                    }
                }
            }
        },
        PanikState::class,
        0f..0.6f,
        InvertedConsideration("Am I dying?", EnemyConsiderations.healthConsideration),
        DoIHaveThisComponentConsideration("Am I burning?", BurningComponent::class)
    )


    val attackTarget = ConsideredActionWithState(
        "Attack Target",
        {},
        { entity, state, deltaTime ->
            //We can reset the cooldown right here to the correct values!
            when (state.status) {
                AttackStatus.Attacking -> {
                    if (state.targetEntity != null && state.ready(deltaTime)) {
                        val agentProperties = AgentProperties.get(entity)
                        val targetEntity = state.targetEntity!!
                        val attackableProperties = AttackableProperties.get(targetEntity)
                        val damage = agentProperties.meleeDamageRange.random()
                        attackableProperties.takeDamage(damage, entity)
                        debug { "Attack dealt $damage" }
                    }
                }
                AttackStatus.NotStarted -> {
                    if (Memory.has(entity)) {
                        val memory = Memory.get(entity)
                        val agentProps = AgentProperties.get(entity)
                        state.coolDownRange = (agentProps.attackSpeed / 1.5f)..(agentProps.attackSpeed * 1.5f)
                        state.coolDown = state.coolDownRange.random()
                        state.targetEntity =
                            memory.closeEntities[PlayerComponent::class.starProjectedType]?.firstOrNull()
                        state.status = AttackStatus.Attacking
                        agentProps.speed = 0f
                    }
                }
            }
        },
        AttackState::class,
        0f..0.9f,
        AmICloseToThisConsideration(PlayerComponent::class, GameConstants.ENEMY_MELEE_DISTANCE)
    )

    val approachTarget = ConsideredActionWithState(
        "Approach Player",
        {},
        { entity, state, deltaTime ->
            when (state.status) {
                ApproachTargetStatus.NotStarted -> {
                    /**
                     * What do we do here? Just set the next status?
                     *
                     * How do we know which thingamajig we see? Do the entire thing
                     * again?
                     *
                     * How do we implement some kind of "memory" for our enemy entity?
                     *
                     * It sees things, then it perhaps remembers where these things are?
                     *
                     * Ah, well, that's what this action does, it checks "again" to see
                     *
                     * players and stores them in some kind of memory. This memory should
                     * probably not only be used for this particular task.
                     *
                     * Or no, the CanISeeConsideration could constantly keep a list of things
                     * the entity sees in some kind of storage, instead.
                     *
                     * We come here because we have seen some shit. So, we take the closest
                     * entity off that list and move towards that one.
                     */
                    if (Memory.has(entity)) {
                        val memory = Memory.get(entity)
                        state.targetEntity =
                            memory.seenEntities[TargetComponent::class.starProjectedType]?.firstOrNull()
                        state.status = ApproachTargetStatus.Approach
                    }
                }
                ApproachTargetStatus.Approach -> {
                    if (state.targetEntity != null) {
                        val agentProps = entity.agentProps()
                        val position = entity.transform().position
                        val targetPosition = state.targetEntity!!.transform().position
                        agentProps.directionVector.set(targetPosition - position).nor()
                        agentProps.speed = agentProps.baseProperties.speed
                    } else {
                        state.status = ApproachTargetStatus.NotStarted
                    }
                }
            }
            debug { "Moving closer to the targets" }
        },
        ApproachTargetState::class,
        0.0f..0.9f,
        CanISeeThisConsideration(PlayerComponent::class)
    )

    val amble = GenericActionWithState(
        "Amble",
        { _ -> 0.7f }, {}, { entity, state, deltaTime ->
            /**
             * What kind of state do we need? Well, we need to check
             * if we are going somewhere, right now, or not.
             *
             * If not, we want to find a place to go to.
             *
             * When we get there, we start over again.
             *
             * Just focus on ambling, right now. Other actions come after
             * this.
             */
            when (state.status) {
                AmbleStatus.FindingPathToTarget -> findPathToTarget(entity, state, deltaTime)
                AmbleStatus.FindingTargetCoordinate -> findTarget(entity, state, deltaTime)
                AmbleStatus.MoveToWaypoint -> move(entity, state)
                AmbleStatus.NotStarted -> {
                    entity.agentProps().speed = 0f
                    state.status =
                        AmbleStatus.FindingTargetCoordinate
                }
                AmbleStatus.NeedsWaypoint -> getWaypoint(entity, state, deltaTime)
            }
        }, AmbleState::class
    )

    private fun getWaypoint(entity: Entity, state: AmbleState, deltaTime: Float) {
        entity.agentProps().apply {
            this.directionVector.rotateDeg(this.rotationSpeed * deltaTime)
            this.speed = 0f
        }
        if (state.wayPoint == null) {
            if (state.ready(deltaTime)) {
                if (state.queue.any()) {
                    state.wayPoint = state.queue.removeFirst()
                    state.status = AmbleStatus.MoveToWaypoint
                } else {
                    state.wayPoint = null
                    state.status = AmbleStatus.NotStarted
                }
            }
        } else {
            state.status = AmbleStatus.MoveToWaypoint
        }
    }

    private fun move(entity: Entity, state: AmbleState) {
        if (state.wayPoint == null) {
            state.status = AmbleStatus.NeedsWaypoint
        } else {
            val currentPos = entity.transform().position
            if (currentPos.dst(state.wayPoint) < TOUCHING_DISTANCE) {
                state.wayPoint = null
                state.status = AmbleStatus.NeedsWaypoint // Safeguard
            } else {
                entity.agentProps().directionVector.set(state.wayPoint!! - currentPos).nor()
                entity.agentProps().speed = entity.agentProps().baseProperties.speed

            }
        }
    }

    private fun findPathToTarget(entity: Entity, state: AmbleState, deltaTime: Float) {
        entity.agentProps().apply {
            this.directionVector.rotateDeg(this.rotationSpeed * deltaTime)
            this.speed = 0f
        }
        if (state.ready(deltaTime)) {
            findPathFromTo(state.queue, state.startPointCoordinate!!, state.endPointCoordinate!!)
            state.status = AmbleStatus.MoveToWaypoint
        }
    }

    private fun findTarget(entity: Entity, state: AmbleState, deltaTime: Float) {
        entity.agentProps().apply {
            this.directionVector.rotateDeg(this.rotationSpeed * deltaTime)
            this.speed = 0f
        }
        if (state.ready(deltaTime)) {
            val position = entity.transform().position
            state.startPointCoordinate = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
            val foundSection = SectionFindingMethods.classicRandom(
                state.startPointCoordinate!!,
                3,
                5
            ) //must it be able to fail? - no, not in this case. if this fail, we randomize
            state.status = AmbleStatus.FindingPathToTarget
            if (foundSection == null) {
                state.endPointCoordinate = SectionFindingMethods.randomOfAll(state.startPointCoordinate!!)
            } else {
                state.endPointCoordinate = foundSection
            }
        }
    }
}

object EnemyConsiderations {
    val healthConsideration = Consideration("How's my health?") { entity ->
        val attackables = entity.attackables()
        MathUtils.norm(0f, attackables.maxHealth, attackables.health)
    }
}