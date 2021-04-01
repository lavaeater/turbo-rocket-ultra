package ai

import ai.builders.*
import com.badlogic.ashley.core.Entity
import ecs.components.ai.NoticedSomething
import ecs.components.ai.*

object Tree {
    fun getEnemyBehaviorTree() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<AttackPlayer> { ifEntityHas<PlayerIsInRange>() })
            then(entityDo<ChasePlayer> { ifEntityHas<TrackingPlayerComponent>()})
            ifThen(
                entityHas<NoticedSomething>(),
                selector {
                    first(entityDo<SeekPlayer>())
                    then(entityDo<Investigate>())
                    then(entityDo<SeekPlayer>())
            })
            last(
            selector<Entity> {
                first(entityDo<Amble>())
                then(invert(entityDo<SeekPlayer>()))
                last(invert(entityDo<ChasePlayer>()))
            })
        }
    }

    fun getEnemyBehaviorTree_old() = tree<Entity> {
        selector<Entity> {
            first(entityDo<Amble>())
            then(invert(entityDo<SeekPlayer>()))
            then(invert(entityDo<ChasePlayer>()))
            last(entityDo<AttackPlayer>())
        }
    }
}