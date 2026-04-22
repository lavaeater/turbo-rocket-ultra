package screens.ui

import ai.treeString
import com.badlogic.gdx.ai.btree.Decorator
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import screens.behavioreditor.label

class TaskNode<T>(val task: Task<T>): com.badlogic.gdx.scenes.scene2d.ui.Tree.Node<TaskNode<T>, Task<T>, Actor>() {
    init {
        isExpanded = true
        isSelectable = true
        value = task
    }

    companion object {

        fun <T>getActorForTask(task: Task<T>) : Actor {
            val returnActor = VerticalGroup()
            returnActor.addActor(label(task.treeString()))
            return returnActor
        }
        fun <T> buildNodeForTask(task: Task<T>): TaskNode<T> {
            val newNode = TaskNode(task)
            newNode.actor
            val returnActor = when (task) {
                is Decorator<T> -> {
                    /*
                    The decorator changes what happens when executing the child
                     */
                    HorizontalGroup()
                }
                else -> {
                    label("Test")
                }
            }
            return newNode
        }
    }

}