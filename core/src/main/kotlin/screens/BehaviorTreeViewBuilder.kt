package screens

import com.badlogic.gdx.ai.btree.Task
import ktx.actors.onClick
import ktx.scene2d.KNode
import ktx.scene2d.label

object BehaviorTreeViewBuilder {
    fun <T> nodeForTask(parent: KNode<*>, task: Task<T>) {
        if (task.guard == null) {
//            parent.horizontalGroup {node ->
//                node.value = task
//                label(task.treeString())
//                textButton("Remove") {
//                    onClick {
//                        (parent.value as Task<T>).addChild()
//                    }
//                }
//            }
            parent.label(task.treeString()) { node ->
                this.onClick {
                    node.isExpanded = !node.isExpanded
                }
                node.isExpanded = true
                node.isSelectable = true
                node {
                    for (childIndex in 0 until task.childCount) {
                        nodeForTask(node, task.getChild(childIndex))
                    }
                }
            }
        } else {
            parent.label("IF ${task.guard.treeString()} THEN") {node ->
                this.onClick {
                    node.isExpanded = !node.isExpanded
                }
                node.isExpanded = true
                node.isSelectable = true
                node {
                    label(task.treeString()) {node->
                        this.onClick {
                            node.isExpanded = !node.isExpanded
                        }
                        node.isExpanded = true
                        node.isSelectable
                        node {
                            for (childIndex in 0 until task.childCount) {
                                nodeForTask(node, task.getChild(childIndex))
                            }
                        }
                    }
                }
            }
        }
    }
}