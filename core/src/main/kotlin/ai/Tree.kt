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
                    expectFailure(fail(lookForAndStore<TargetComponent, SeenPlayerPositions>()))
                })
                    .ifThis(entityDoesNotHave<SeenPlayerPositions>())

                doThis(invertResultOf(moveTowardsPositionTarget<AttackPoint>(run = true)))
                    .ifThis(entityHas<AttackPoint>())

                doThis(selectTarget<SeenPlayerPositions, AttackPoint>())
                    .ifThis(entityHas<SeenPlayerPositions>())

                doThis(invertResultOf(moveTowardsPositionTarget<Waypoint>()))
                    .ifThis(entityHas<Waypoint>())
            }
        )
    }

    fun testTree() = tree<Entity> {
        root(
            repeatForever(
                exitOnFirstThatSucceeds {
                    expectedToSucceed(
                        onlyIfEntityHas<Path>(
                            exitOnFirstThatSucceeds {
                                expectFailureAndMoveToNext(
                                    onlyIfEntityHas<PositionTarget>(invertResultOf(moveTowardsPositionTarget<Waypoint>()))
                                )
                                expectFailureAndMoveToNext(
                                    invertResultOf(
                                        repeat(10, exitOnFirstThatSucceeds {
                                            expectFailureAndMoveToNext(invertResultOf(rotate(15f)))
                                            expectedToSucceed(lookForAndStore<ObstacleComponent, SeenPlayerPositions>())
                                        })
                                    )
                                )
                                expectedToSucceed(
                                    onlyIfEntityDoesNotHave<PositionTarget>(getNextStepOnPath())
                                )
                            }
                        )
                    )
                    expectedToSucceed(
                        onlyIfEntityDoesNotHave<Path>(
                            exitOnFirstThatFails {
                                first(findSection<AmblingEndpoint>())
                                then(findPathTo<AmblingEndpoint>())
                            }
                        )
                    )
                })
        )
    }

    fun getTowerBehaviorTree() = tree<Entity> {
        add(runInTurnUntilFirstFailure {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        })
    }
}