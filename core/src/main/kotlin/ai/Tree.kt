package ai

import ai.builders.*
import com.badlogic.ashley.core.Entity
import ecs.components.ai.*
import ecs.components.gameplay.ObstacleComponent
import ecs.components.towers.FindTarget
import ecs.components.towers.Shoot
import ecs.components.towers.TargetInRange

object Tree {
    fun testTree() = tree<Entity> {
        root(
            repeatForever(
                runUntilFirstSucceeds {
                    expectSuccess(
                        onlyIfEntityHas<Path>(
                            runUntilFirstSucceeds {
                                expectFailureAndMoveToNext(
                                    onlyIfEntityHas<PositionTarget>(invertResultOf(moveTowardsPositionTarget()))
                                )
                                expectFailureAndMoveToNext(
                                    invertResultOf(
                                        repeat(10, runUntilFirstSucceeds {
                                            expectFailureAndMoveToNext(invertResultOf(rotate(15f)))
                                            expectSuccess(lookForAndStore<ObstacleComponent, SeenPlayerPositions>())
                                        })
                                    )
                                )
                                expectSuccess(
                                    onlyIfEntityDoesNotHave<PositionTarget>(getNextStepOnPath())
                                )
                            }
                        )
                    )
                    expectSuccess(
                        onlyIfEntityDoesNotHave<Path>(
                            runInSequence {
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