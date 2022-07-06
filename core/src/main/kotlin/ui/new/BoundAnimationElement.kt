package ui.new

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import eater.physics.drawScaled

open class BoundAnimationElement<T: Any>(valueFunc: (T) -> Animation<Sprite>,
                                         val changeAnimation: (T) -> Unit = {},
                                         position: Vector2 = vec2(),
                                         var scale: Float = 1f,
                                         var rotation: Float = 0f,
                                         parent: AbstractElement? = null) :
    BoundElement<T, Animation<Sprite>>(valueFunc, position, parent) {

    private var animationStateTime: Float = 0f
    private var changeAnimCooldown = 5f

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        super.render(batch, delta, scale, debug)
        animationStateTime += delta
        changeAnimCooldown -= delta
        if(changeAnimCooldown <= 0f) {
            changeAnimation(currentItem)
            changeAnimCooldown = 5f
        }
        val sprite = valueFunc(currentItem).getKeyFrame(animationStateTime)

        batch.drawScaled(
            sprite,
            actualPosition.x + (sprite.regionWidth / 2 * scale),
            actualPosition.y + (sprite.regionHeight / 2 * scale),
            scale,
            rotation
        )
    }
}