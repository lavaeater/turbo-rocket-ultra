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
        add(
            repeatForever(
                selector {
                    selector {
                        first(invert(rotate(5f)))
                        then(invert(delayFor(1f)))
                        then(invert(lookForAndStore<ObstacleComponent, SeenPlayerPositions>()))
                    }
                    then(invert(sequence {
                        first(findSection<AmblingEndpoint>())
                        then(findPathTo<AmblingEndpoint>())
                    }))
                    then(
                        ifEntityHas<Path>



                            invert(
                                ai.builders.selector {
                                    ifEntityHas()
                                    selector {
                                        first(invert(entityHas<PositionTarget>()))
                                        then(invert(moveTowardsPositionTarget()))
                                    }
                                    selector {
                                        first(entityHas<PositionTarget>())
                                        then(invert(getNextStepOnPath()))
                                    }
                                    /*
                                    The above succeeding means we end up here, implying we have
                                    reached the end of the path. So what do we do now then?
                                     */
                                }
                        )
                    ))
                })
        )
    }

    fun getTowerBehaviorTree() = tree<Entity> {
        add(sequence {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        })
    }
}