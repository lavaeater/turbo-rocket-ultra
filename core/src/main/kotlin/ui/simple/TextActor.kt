package ui.simple

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Assets

open class TextActor(
    open val text: String,
    position: Vector2 = vec2()) : LeafActor(position) {
    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {
        Assets.font.draw(
            batch,
            text,
            parentPosition.x + position.x,
            parentPosition.y - position.y
        )
    }

}