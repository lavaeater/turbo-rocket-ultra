package ai

import ai.builders.*
import ai.tasks.EntityDecorator
import ai.tasks.invertDecorator
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
        add(repeatForever(selector<Entity> {
            first(invert(rotate(15f)))
            then(invert(delayFor(1f)))
            then(invert(lookForAndStore<ObstacleComponent, SeenPlayerPositions>()))
        }))

    }

    fun getTowerBehaviorTree() = tree<Entity> {
        sequence {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        }
    }
}