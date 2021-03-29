package ai

import ai.builders.*
import com.badlogic.ashley.core.Entity
import ecs.components.ai.NoticedSomething
import ecs.components.ai.*

object Tree {
    fun getEnemyBehaviorTree() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<Investigate> { ifEntityHas<NoticedSomething>() })
            then(entityDo<AttackPlayer> { ifEntityHas<PlayerInRangeComponent>() })
            then(ai.builders.selector {
                first(invert(entityDo<SeekPlayer>{ unlessEntityHas<NoticedSomething>()}))
                last(invert(entityDo<ChasePlayer>{ unlessEntityHas<NoticedSomething>() }))
            })
            then(entityDo<Amble> { unlessEntityHas<NoticedSomething>()})
        }
    }
    fun getEnemyBehaviorTree_old() = tree<Entity> {
        selector {
            first(entityDo<Amble>())
            then(invert(entityDo<SeekPlayer>()))
            then(invert(entityDo<ChasePlayer>()))
            last(entityDo<AttackPlayer>())
        }
    }
}