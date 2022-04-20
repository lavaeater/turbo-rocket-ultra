package ai

import ai.builders.*
import com.badlogic.ashley.core.Entity
import ecs.components.ai.NoticedSomething
import ecs.components.ai.*
import ecs.components.ai.boss.*
import ecs.components.gameplay.BurningComponent
import ecs.components.towers.FindTarget
import ecs.components.towers.Shoot
import ecs.components.towers.TargetInRange

object Tree {
    fun getEnemyBehaviorTree() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<Panic> { ifEntityHas<BurningComponent>() })
            //then(entityDo<AttackPlayer> { ifEntityHas<PlayerIsInRange>() })
            //then(entityDo<ChasePlayer> { ifEntityHas<TrackingPlayer>() })
            ifThen(
                entityHas<NoticedSomething>(),
                selector {
                    first(entityDo<SeekPlayer>())
                    then(entityDo<Investigate>())
                    then(entityDo<SeekPlayer>())
                })
            last(
                selector<Entity> {
                    first(invert(entityDo<Amble>()))
                    then(invert(entityDo<SeekPlayer>()))
                    //last(invert(entityDo<ChasePlayer>()))
                })
        }
    }

    /**
     * The boss...
     *
     * The boss only walks in north, south or east directions (all enemies should do this, for fun)
     *
     * After walking for some time, he stops and looks for players. He picks the closest and rushes
     * towards the player. Any players hit during this suffer tackle damage and are pushed around
     * and about in some directions.
     *
     * His other attack is a grab and throw attack. A player getting close enough when he is walking
     * around might be grabbed and then thrown, suffering throw damage.
     */
    fun bossOne() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<RushPlayer> { ifEntityHas<TrackingPlayer>() })
            ifThen(
                entityHas<NoticedSomething>(),
                selector {
                    first(entityDo<SeekPlayer>())
                    then(entityDo<Investigate>())
                    then(entityDo<SeekPlayer>())
                })
            last(selector<Entity> {
                first(invert(entityDo<Amble>()))
                then(invert(entityDo<SeekPlayer>()))
            })
        }
    }


    fun getTowerBehaviorTree() = tree<Entity> {
        sequence {
            first(entityDo<FindTarget>())
            then(entityDo<Shoot> { ifEntityHas<TargetInRange>() })
        }
    }
}