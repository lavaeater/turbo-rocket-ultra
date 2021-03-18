package tru

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class LpcCharacterAnim(val state: AnimState, val animations: Map<SpriteDirection, Animation<TextureRegion>>)