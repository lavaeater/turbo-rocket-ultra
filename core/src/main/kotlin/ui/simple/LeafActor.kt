package ui.simple

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

abstract class LeafActor(val position: Vector2 = vec2()) : SimpleActor {
    abstract override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean)
}