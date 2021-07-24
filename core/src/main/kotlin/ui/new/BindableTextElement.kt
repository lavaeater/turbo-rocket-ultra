package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

open class BindableTextElement(
    val valueFunc: () -> String,
    position: Vector2 = vec2(),
    parent: AbstractElement? = null) : AbstractElement(position, parent = parent) {

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        super.render(batch, delta, scale, debug)
        val toWrite = valueFunc() as CharSequence

        Assets.font.draw(
            batch,
            toWrite,
            actualPosition.x,
            actualPosition.y
        )
    }
}