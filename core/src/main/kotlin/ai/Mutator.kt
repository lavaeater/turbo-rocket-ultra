package ai

import ai.tasks.EntityDecorator
import ai.tasks.invertDecorator
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.*
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.ai.btree.decorator.Repeat
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution

object Mutator {
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
}