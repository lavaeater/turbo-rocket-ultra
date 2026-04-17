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
import ai.behaviorTree.builders.invertResultOf
import ai.behaviorTree.builders.lookForAndStore
import ai.behaviorTree.builders.moveTowardsPositionTarget
import ai.behaviorTree.builders.rotate
import ai.behaviorTree.builders.runInTurnUntilFirstFailure
import ai.behaviorTree.builders.selectTarget
import ai.behaviorTree.builders.tree
import ai.behaviorTree.builders.tryInTurn
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.AmblingEndpoint
import ecs.components.ai.AttackPoint
import ecs.components.ai.Path
import ecs.components.ai.SeenPlayerPositions
import ecs.components.ai.Waypoint
import ecs.components.player.PlayerComponent
import ecs.components.towers.FindTarget
import ecs.components.towers.Shoot
import ecs.components.towers.TargetInRange

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
                    expectSuccess(findSection<AmblingEndpoint>())
                    expectFailure(invertResultOf(findPathTo<AmblingEndpoint>()))
                })
                    .ifThis(entityDoesNotHave<Path>())

                doThis(invertResultOf(getNextStepOnPath()))
                    .ifThis(entityDoesNotHave<Waypoint>())

                doThis(runInTurnUntilFirstFailure {
                    expectSuccess(rotate(15f))
                    expectFailure(fail(lookForAndStore<PlayerComponent, SeenPlayerPositions>(true)))
                })
                    .ifThis(entityDoesNotHave<SeenPlayerPositions>())

                doThis(
                    tryInTurn {
                        expectFailureAndMoveToNext(invertResultOf(moveTowardsPositionTarget<AttackPoint>(run = true)))
                        expectFailureAndMoveToNext(invertResultOf(attack<PlayerComponent>()))
                    }
                )
                    .ifThis(entityHas<AttackPoint>())

                doThis(selectTarget<SeenPlayerPositions, AttackPoint>())
                    .ifThis(entityHas<SeenPlayerPositions>())

                doThis(invertResultOf(moveTowardsPositionTarget<Waypoint>()))
                    .ifThis(entityHas<Waypoint>())
            }
        )
    }

    fun getTowerBehaviorTree() = tree<Entity> {
        add(runInTurnUntilFirstFailure {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        })
    }
}