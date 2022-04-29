package ui.simple

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

interface SimpleActor {
    fun render(batch: Batch, parentPosition: Vector2 = vec2(), debug: Boolean = false)
}