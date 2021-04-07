package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

open class BoundTextElement<T:Any>(valueFunc: (T) -> String, position: Vector2 = vec2(), parent: AbstractElement? = null) :
    BoundElement<T, String>(valueFunc, position, parent) {

    override fun render(batch: Batch, delta: Float, debug: Boolean) {
        super.render(batch, delta, debug)
        Assets.font.draw(
            batch,
            valueFunc(currentItem) as CharSequence,
            actualPosition.x,
            actualPosition.y
        )
    }
}