package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue

class FiredShotsComponent : Component {
    val queue = Queue<Vector2>()
}