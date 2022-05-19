package ai

import ai.builders.*
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.*
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TargetComponent
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
                    expectFailure(fail(lookForAndStore<TargetComponent, SeenPlayerPositions>(true)))
                })
                    .ifThis(entityDoesNotHave<SeenPlayerPositions>())

                doThis(
                    tryInTurn {
                        expectFailureAndMoveToNext(invertResultOf(moveTowardsPositionTarget<AttackPoint>(run = true)))
                        expectFailureAndMoveToNext(invertResultOf(attack<TargetComponent>()))
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