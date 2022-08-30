package screens.stuff

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import isometric.toIsometric
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
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

class AnimatedSpriteNode3d(
    name: String,
    texture: Texture,
    localPosition: Vector3 = vec3(),
    parent: Node3d? = null,
    color: Color = Color.WHITE,
    updateActions: MutableList<(Node3d, Float) -> Unit> = mutableListOf()
) : Node3d(name, localPosition, parent, color, updateActions) {
    val sprite = AnimatedSprite(texture)
    override fun drawIso(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float, recursive: Boolean, offset: Vector2) {
        val spritePos = isoPosition - sprite.offset + offset.toIsometric()
        batch.draw(sprite, spritePos.x, spritePos.y)
        if (recursive)
            for (childNode in children) {
                childNode.drawIso(batch, shapeDrawer, delta, true, offset)
            }
    }

    override fun draw2d(
        batch: Batch,
        shapeDrawer: ShapeDrawer,
        delta: Float,
        recursive: Boolean,
        offset: Vector2,
        zUp: Boolean
    ) {

        val spritePos = if (zUp) vec2(
            globalPosition3d.x - sprite.offset.x + offset.x,
            globalPosition3d.z - sprite.offset.y + offset.y
        ) else vec2(globalPosition3d.x - sprite.offset.x + offset.x, globalPosition3d.y - sprite.offset.y + offset.y)
        batch.draw(sprite, spritePos.x, spritePos.y)
        shapeDrawer.filledCircle(spritePos + sprite.offset, 1f, color)
        if (recursive)
            for (childNode in children) {
                childNode.draw2d(batch, shapeDrawer, delta, true, offset, zUp)
            }
    }
}