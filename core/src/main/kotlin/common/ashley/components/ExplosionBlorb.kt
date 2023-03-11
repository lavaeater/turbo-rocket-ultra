package common.ashley.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

data class ExplosionBlorb(val position: Vector2, val startRadius: Float = 1f, val endRadius: Float = 15f, val backColor: Color = Color(
    1f,
    0f,
    0f,
    0.5f
), val frontColor: Color = Color(0.7f, 0.4f, 0f, 0.5f)
) {
    var currentRadius = startRadius
}