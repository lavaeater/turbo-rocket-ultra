package ai.behaviorTree

import ai.behaviorTree.tasks.EntityDecorator
import ai.behaviorTree.tasks.invertDecorator
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.BranchTask
import com.badlogic.gdx.ai.btree.Decorator
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Parallel
import com.badlogic.gdx.ai.btree.branch.RandomSelector
import com.badlogic.gdx.ai.btree.branch.RandomSequence
import com.badlogic.gdx.ai.btree.branch.Selector
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.ai.btree.decorator.Repeat
import ai.behaviorTree.tasks.leaf.DelayTask
import ai.behaviorTree.tasks.leaf.RotateTask
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import kotlin.reflect.full.createInstance

object Mutator {
    fun getMutatedTree(tree: BehaviorTree<Entity>): BehaviorTree<Entity> {
        val newTree = BehaviorTree<Entity>()
        if (tree.childCount > 0) {
            newTree.addChild(mutateTask(tree.getChild(0)))
        }
        return newTree
    }

    private fun mutateBrancher(task: BranchTask<Entity>) : BranchTask<Entity> {
        val possibleClasses = listOf(Selector::class, com.badlogic.gdx.ai.btree.branch.Sequence::class, RandomSelector::class, RandomSequence::class, Parallel::class, DynamicGuardSelector::class).minus(task::class)
        val c = possibleClasses.random()
        val newBranchTask = c.createInstance() as BranchTask<Entity>
        if(task.guard != null) {
            newBranchTask.guard = task.guard.cloneTask()
        }
        for(index in 0 until task.childCount) {
            val newChild = mutateTask(task.getChild(index))
            newBranchTask.addChild(newChild)
        }
        return newBranchTask
    }

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
            is BranchTask<Entity> -> mutateBrancher(task)
            is Decorator<Entity> -> mutateDecorator(task)
            is LeafTask<Entity> -> mutateLeaf(task)
            else -> {
                Invert()
            }
        }
    }

    private fun mutateLeaf(task: LeafTask<Entity>): LeafTask<Entity> {
        return when (task) {
            is DelayTask -> DelayTask((2..50).random() / 10f)
            is RotateTask -> RotateTask((15..360).random().toFloat(), listOf(true, false).random())
            else -> task.cloneTask() as LeafTask<Entity>
        }
    }
}