package ai

import ai.builders.entityDo
import ai.builders.invert
import ai.builders.tree
import com.badlogic.ashley.core.Entity
import ecs.components.ai.Amble
import ecs.components.ai.AttackPlayer
import ecs.components.ai.ChasePlayer
import ecs.components.ai.SeekPlayer

object Tree {
    fun getEnemyBehaviorTree() = tree<Entity> {
        selector {
            first(entityDo<Amble>())
            then(invert(entityDo<SeekPlayer>()))
            then(invert(entityDo<ChasePlayer>()))
            last(entityDo<AttackPlayer>())
        }
    }
}