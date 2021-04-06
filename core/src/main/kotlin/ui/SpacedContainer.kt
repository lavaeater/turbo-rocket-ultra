package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class SpacedContainer(private val offset: Vector2, position: Vector2, root: Boolean = false) :
    ContainerBaseActor(position, root) {
    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {
        for ((index, child) in children.withIndex()) {
            if (root)
                child.render(
                    batch,
                    spaceVector.set(
                        position.x + index * offset.x,
                        position.y - index * offset.y
                    )
                )
            else
                child.render(
                    batch,
                    spaceVector.set(
                        parentPosition.x + position.x + index * offset.x,
                        parentPosition.y - position.y - index * offset.y
                    )
                )
        }
    }
}