package ai

import ai.builders.entityDo
import ai.builders.invert
import ai.builders.tree
import com.badlogic.ashley.core.Entity
import ecs.components.ai.Amble
import ecs.components.ai.ChasePlayer
import ecs.components.ai.SeekPlayer

object Tree {
    fun bt() = tree<Entity> {
        selector {
            first(entityDo<Amble>())
            then(invert(entityDo<SeekPlayer>()))
            last(entityDo<ChasePlayer>())
        }
    }
}