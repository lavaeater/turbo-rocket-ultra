package tru

import com.badlogic.gdx.graphics.g2d.Animation
import eater.input.CardinalDirection

//@Serializable
class AnimDef(val state: AnimState, val direction: CardinalDirection, val row: Int, val startCol: Int, val endCol: Int)

class LpcCharacterAnim<T>(val state: AnimState, val animations: Map<CardinalDirection, Animation<T>>)