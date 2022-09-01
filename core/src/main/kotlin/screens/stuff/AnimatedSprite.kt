package screens.stuff

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.IThing

/**
 * My graphics can look any way they want - as long as they are unique and interesting.
 *
 * So, lets try triangles for legs,
 *
 * Lets try it out by drawing just dots and stuff for now.
 */

open class AnimatedSprite(texture: Texture) : TextureRegion(texture) {
    /*
    We assume a center based origin at first.
     */
    val position = vec2()
    val offset = vec2(texture.width / 2f, texture.height / 2f)
    val actualPosition: Vector2
        get() {
            return position - offset
        }
}

class AnimatedSprited3d(texture: Texture, val localPosition:Vector3 = vec3(), val parent: IThing): AnimatedSprite(texture) {
    val position3d get() = parent.position + localPosition
}