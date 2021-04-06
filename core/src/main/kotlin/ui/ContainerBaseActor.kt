package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

abstract class ContainerBaseActor(val position: Vector2, val root: Boolean = false) : SimpleActor {
    val children = mutableListOf<SimpleActor>()
    val spaceVector = vec2()
    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {
        for (child in children) {
            if(root)
                child.render(batch, spaceVector.set(position.x, position.y))
            else
                child.render(batch, spaceVector.set(position.x + parentPosition.x, parentPosition.y - position.y))
        }
    }
}