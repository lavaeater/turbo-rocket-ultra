package ai

import ai.builders.*
import ai.tasks.EntityDecorator
import ai.tasks.invertDecorator
import ai.tasks.leaf.AmblingEndpointComponent
import ai.tasks.leaf.FindSection
import ai.tasks.leaf.PathComponent
import ai.tasks.leaf.SeenPlayerPositions
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.*
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.ai.btree.decorator.Repeat
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import ecs.components.ai.*
import ecs.components.ai.boss.RushPlayer
import ecs.components.gameplay.BurningComponent
import ecs.components.gameplay.ObstacleComponent
import ecs.components.player.PlayerComponent
import ecs.components.towers.FindTarget
import ecs.components.towers.Shoot
import ecs.components.towers.TargetInRange

object Tree {
    fun testTree() = tree<Entity> {
        add(
            repeatForever(
                selector {
                    first(selector {
                        first(invert(rotate(5f)))
                        then(invert(delayFor(1f)))
                        then(invert(lookForAndStore<ObstacleComponent, SeenPlayerPositions>()))
                    })
                    then(invert(sequence {
                        first(findSection<AmblingEndpointComponent>())
                        then(findPathTo<AmblingEndpointComponent>())
                    }))
                    then(ifEntityHas<PathComponent>(selector {
                        /*
                        Follow a path code goes here
                         */
                    }))
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