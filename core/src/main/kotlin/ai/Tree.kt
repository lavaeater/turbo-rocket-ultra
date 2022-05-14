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
    fun getMutatedTree(tree: BehaviorTree<Entity>): BehaviorTree<Entity> {
        val newTree = BehaviorTree<Entity>()
        if (tree.childCount > 0) {
            newTree.addChild(mutateTask(tree.getChild(0)))
        }
        return newTree
    }

//    private fun mutateBrancher(task: BranchTask<Entity>) : BranchTask<Entity> {
//        val possibleClasses = listOf(Selector::class, com.badlogic.gdx.ai.btree.branch.Sequence::class, RandomSelector::class, RandomSequence::class, Parallel::class, DynamicGuardSelector::class).minus(task::class)
//        val c = possibleClasses.random()
//        val newBranchTask = c.createInstance() as BranchTask<Entity>
//        if(task.guard != null) {
//            newBranchTask.guard = task.guard.cloneTask()
//        }
//        for(index in 0 until task.childCount) {
//            val newChild = mutateTask(task.getChild(index))
//            newBranchTask.addChild(newChild)
//        }
//        return newBranchTask
//    }

    private fun mutateDecorator(task: Decorator<Entity>): Task<Entity> {
        val newTask = when (task) {
            is EntityDecorator -> task.invertDecorator()
            is AlwaysFail -> AlwaysSucceed(mutateTask(task.getChild(0)))
            is AlwaysSucceed -> AlwaysFail(mutateTask(task.getChild(0)))
            is Repeat -> mutateRepeat(task)
            is Invert -> task.getChild(0)
            else -> {
                task
            }
        }
        if (task.guard != null) {
            newTask.guard = task.guard.cloneTask()
        }
        return newTask
    }

    private fun mutateRepeat(task: Repeat<Entity>): Repeat<Entity> {
        task.times = ConstantIntegerDistribution((5..50).random())
        return task
    }

    fun mutateTask(task: Task<Entity>): Task<Entity> {
        return when (task) {
            is BranchTask<Entity> -> task
            is Decorator<Entity> -> mutateDecorator(task)
            is LeafTask<Entity> -> mutateLeaf(task)
            else -> {
                Invert()
            }
        }
    }

    private fun mutateLeaf(task: LeafTask<Entity>): LeafTask<Entity> {
        return task
    }

    fun getEnemyBehaviorTree(): BehaviorTree<Entity> {
        val t = tree<Entity> {
            dynamicGuardSelector {
                first(entityDo<Panic> { ifEntityHas<BurningComponent>() })
                then(entityDo<AttackPlayer> { ifEntityHas<PlayerIsInRange>() })
                then(entityDo<ChasePlayer> { ifEntityHas<IsAwareOfPlayer>() })
                ifThen(
                    entityHas<NoticedSomething>(),
                    selector {
                        first(canSee<PlayerComponent>())
                        then(entityDo<Investigate>())
                        then(canSee<PlayerComponent>())
                    })
                last(
                    selector<Entity> {
                        first(invert(entityDo<Amble> { unlessEntityHas<IsAwareOfPlayer>() }))
                        then(invert(canSee<PlayerComponent>()))
                        last(invert(entityDo<ChasePlayer>()))
                    })
            }
        }
        return t
    }

    fun testTree() = tree<Entity> {
        add(repeatForever(selector<Entity> {
            first(invert(rotate(15f)))
            then(invert(delayFor(1f)))
            then(invert(lookForAndStore<ObstacleComponent, SeenPlayerPositions>()))
        }))

    }

    fun getEnemyBehaviorThatFindsOtherEnemies() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<Panic> { ifEntityHas<BurningComponent>() })
            then(entityDo<AttackPlayer> { ifEntityHas<PlayerIsInRange>() })
            then(entityDo<AlertFriends> { ifEntityHas<IsAwareOfPlayer>() })
            ifThen(
                entityHas<NoticedSomething>(),
                selector {
                    first(entityDo<SeekPlayer>())
                    then(entityDo<Investigate>())
                    then(entityDo<SeekPlayer>())
                })
            last(
                selector<Entity> {
                    first(invert(entityDo<Amble> { unlessEntityHas<IsAwareOfPlayer>() }))
                    then(invert(entityDo<SeekPlayer>()))
                    last(invert(entityDo<ChasePlayer>()))
                })
        }
    }

    fun bossTest() = tree<Entity> {
        add(repeatForever(canSee<PlayerComponent>()))
    }

    fun bossOne() = tree<Entity> {
        dynamicGuardSelector {
            first(entityDo<RushPlayer> { ifEntityHas<IsAwareOfPlayer>() })
            ifThen(
                entityHas<NoticedSomething>(),
                selector {
                    first(canSee<PlayerComponent>())
                    then(entityDo<Investigate>())
                    then(canSee<PlayerComponent>())
                })
            last(selector<Entity> {
                first(invert(entityDo<Amble>()))
                then(invert(canSee<PlayerComponent>()))
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