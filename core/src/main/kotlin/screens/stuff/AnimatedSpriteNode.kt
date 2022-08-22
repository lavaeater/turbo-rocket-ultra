package screens.stuff

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import isometric.toIsometric
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

/**
 * I realize now that the character or whatever has to be considered
 * from above when projecting it in isometric. So the offset of the eyes on a rotating
 * head has to be considered in one way, and then then head rotating in a different way...
 *
 * But if we put everything arranged using a top-down view and also consider the
 * z-coordinate, we should be fine
 *
 */

class AnimatedSpriteNode(texture: Texture) : Node() {
    val sprite = AnimatedSprite(texture)
    override fun drawIso(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        val spritePos = vec2(actualPosition.x - sprite.offset.x, actualPosition.y - sprite.offset.y).toIsometric()
        batch.draw(sprite, spritePos.x, spritePos.y)
//        shapeDrawer.filledCircle(spritePos, 1f, color)
        for (childNode in children) {
            childNode.drawIso(batch, shapeDrawer, delta)
        }
    }

    override fun draw(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        val spritePos = vec2(actualPosition.x - sprite.offset.x, actualPosition.y - sprite.offset.y)
        batch.draw(sprite, spritePos.x, spritePos.y)
//        shapeDrawer.filledCircle(actualPosition, 1f, color)
        for (childNode in children) {
            childNode.draw(batch, shapeDrawer, delta)
        }
    }
}