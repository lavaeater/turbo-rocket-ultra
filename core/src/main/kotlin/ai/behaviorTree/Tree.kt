package ai.behaviorTree

import ai.behaviorTree.builders.attack
import ai.behaviorTree.builders.entityDo
import ai.behaviorTree.builders.entityDoesNotHave
import ai.behaviorTree.builders.entityHas
import ai.behaviorTree.builders.exitOnFirstThatFails
import ai.behaviorTree.builders.exitOnFirstThatSucceeds
import ai.behaviorTree.builders.fail
import ai.behaviorTree.builders.findPathTo
import ai.behaviorTree.builders.findSection
import ai.behaviorTree.builders.getNextStepOnPath
import ai.behaviorTree.builders.grabAndThrowPlayer
import ai.behaviorTree.builders.invertResultOf
import ai.behaviorTree.builders.lookForAndStore
import ai.behaviorTree.builders.moveTowardsPositionTarget
import ai.behaviorTree.builders.playerIsInGrabRange
import ai.behaviorTree.builders.rotate
import ai.behaviorTree.builders.rushPlayer
import ai.behaviorTree.builders.runInTurnUntilFirstFailure
import ai.behaviorTree.builders.selectTarget
import ai.behaviorTree.builders.tree
import ai.behaviorTree.builders.tryInTurn
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.ai.AttackPoint
import components.ai.Path
import components.ai.SeenPlayerPositions
import components.ai.Waypoint
import components.player.PlayerComponent
import components.towers.TargetInRange

fun <T> useThisGuard(task: Task<T>): Task<T> {
    return task
}

fun <T> Task<T>.then(task: Task<T>): Task<T> {
    task.guard = this
    return task
}

fun <T> Task<T>.ifThis(task: Task<T>) {
    this.guard = task
}

object Tree {
    fun nowWithAttacks() = tree<Entity> {
        root(
            exitOnFirstThatSucceeds {

                doThis(exitOnFirstThatFails {
                    expectSuccess(findSection<components.ai.AmblingEndpoint>())
                    expectFailure(invertResultOf(findPathTo<components.ai.AmblingEndpoint>()))
                })
                    .ifThis(entityDoesNotHave<Path>())

                doThis(invertResultOf(getNextStepOnPath()))
                    .ifThis(entityDoesNotHave<Waypoint>())

                doThis(runInTurnUntilFirstFailure {
                    expectSuccess(rotate(15f))
                    expectFailure(fail(lookForAndStore<PlayerComponent, components.ai.SeenPlayerPositions>(true)))
                })
                    .ifThis(entityDoesNotHave<SeenPlayerPositions>())

                doThis(
                    tryInTurn {
                        expectFailureAndMoveToNext(invertResultOf(moveTowardsPositionTarget<AttackPoint>(run = true)))
                        expectFailureAndMoveToNext(invertResultOf(attack<PlayerComponent>()))
                    }
                )
                    .ifThis(entityHas<AttackPoint>())

                doThis(selectTarget<components.ai.SeenPlayerPositions, AttackPoint>())
                    .ifThis(entityHas<SeenPlayerPositions>())

                doThis(invertResultOf(moveTowardsPositionTarget<Waypoint>()))
                    .ifThis(entityHas<Waypoint>())
            }
        )
    }

    fun bossBehaviorTree() = tree<Entity> {
        root(
            exitOnFirstThatSucceeds {
                // Highest priority: grab the player if right on top of them
                doThis(grabAndThrowPlayer(grabRange = 2f, stunDuration = 1.5f, throwForce = 25f, damage = 30f))
                    .ifThis(playerIsInGrabRange(2f))

                // Rush: charge at the player's position at the moment of decision (dodgeable)
                doThis(rushPlayer(damage = 20f))

                // Fallback: look around for the player
                doThis(runInTurnUntilFirstFailure {
                    expectSuccess(rotate(30f))
                    expectFailure(fail(lookForAndStore<PlayerComponent, SeenPlayerPositions>(false)))
                })
                    .ifThis(entityDoesNotHave<SeenPlayerPositions>())
            }
        )
    }

    fun getTowerBehaviorTree() = tree<Entity> {
        add(runInTurnUntilFirstFailure {
            first(entityDo<components.towers.FindTarget>())
            then(entityDo<components.towers.Shoot> { ifEntityHas<TargetInRange>() })
        })
    }
}