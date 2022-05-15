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
                succeedOnFirstSuccessMoveToNextOnFailure {
                    moveToNextIfThisFails(
                        succeedOnFirstSuccessMoveToNextOnFailure {
                            moveToNextIfThisFails(invertResultFrom(rotate(5f)))
                            moveToNextIfThisFails(invertResultFrom(delayFor(1f)))
                            failBranchIfThisFails(invertResultFrom(lookForAndStore<ObstacleComponent, SeenPlayerPositions>()))
                        })
                    moveToNextIfThisFails(
                        ifEntityDoesNotHaveThisComponent<Path>(
                            invertResultFrom(
                                failOnFirstFailureMoveToNextOnSuccess {
                                    moveToNextIfThisSucceeds(findSection<AmblingEndpoint>())
                                    branchSucceedsIfThisSucceeds(findPathTo<AmblingEndpoint>())
                                })
                        )
                    )
                    failBranchIfThisFails(
                        ifEntityHasThisComponent<Path>(
                            invertResultFrom(
                                succeedOnFirstSuccessMoveToNextOnFailure {
                                    moveToNextIfThisFails(
                                        ifEntityHasThisComponent<PositionTarget>(invertResultFrom(moveTowardsPositionTarget()))
                                    )
                                    failBranchIfThisFails(
                                        ifEntityDoesNotHaveThisComponent<PositionTarget>(invertResultFrom(getNextStepOnPath()))
                                    )
                                })
                        )
                    )
                })
        )
    }

    fun getTowerBehaviorTree() = tree<Entity> {
        add(failOnFirstFailureMoveToNextOnSuccess {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        })
    }
}