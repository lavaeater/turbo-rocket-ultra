package ai

import ai.builders.doesEntityHave
import ai.builders.entityDo
import ai.builders.invert
import ai.builders.tree
import com.badlogic.ashley.core.Entity
import ecs.components.PlayerIsInSensorRangeComponent
import ecs.components.ai.*

object Tree {
    fun getEnemyBehaviorTree() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<Amble>())
            then(invert(entityDo<SeekPlayer>()))
            then(invert(entityDo<ChasePlayer>()))
            last(entityDo<AttackPlayer>())
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