package screens.ui

import com.badlogic.gdx.math.Vector2
import ktx.math.plus
import ktx.math.vec2

class GraphicsNode(localX: Float = 0.5f, localY: Float = 0.5f, var rotation: Float = 0f, var parent: GraphicsNode? = null) {
    var localX: Float
        set(value) {
            localPosition.x = value
        }
        get() = localPosition.x
    var localY: Float
        set(value) {
            localPosition.y = value
        }
        get() = localPosition.y

    val localPosition = vec2(localX, localY)
    val globalPosition: Vector2
        get() {
            if (parent == null) {
                return localPosition
            } else {
                /*
                Intriguing

                What does this even mean?
                The position of the child node is relative to the parent node.
                If the parent rotates, this affects where the child is henceforth.
                Ah, I know. The localX and localY are what we ADD to the parent
                location to get global location. Tha means that if we rotate the parent, we
                will automagically get the children rotated, right? So the child position IS
                basically the vector from parent to child!
                And this distance is always normalized since we calculate it?
                 */
                return parent!!.globalPosition + localPosition
            }
        }
    val children = mutableListOf<GraphicsNode>()
    fun addChild(globalX: Float, globalY: Float) : GraphicsNode {
        val lp = vec2(globalX, globalY).sub(globalPosition)
        val newChild = GraphicsNode(lp.x, lp.y, 0f, this)
        children.add(newChild)
        return newChild
    }

    val allChildren:List<GraphicsNode> get() = children + children.flatMap { it.allChildren }
}