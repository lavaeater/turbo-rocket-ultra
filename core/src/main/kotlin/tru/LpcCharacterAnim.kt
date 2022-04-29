package tru

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
//import kotlinx.serialization.Serializable

//@Serializable
class AnimDef(val state: AnimState, val direction: SpriteDirection, val row: Int, val startCol: Int, val endCol: Int)

class LpcCharacterAnim<T>(val state: AnimState, val animations: Map<SpriteDirection, Animation<T>>)