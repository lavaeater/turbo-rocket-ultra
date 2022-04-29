package ecs.components.ai

import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import ktx.math.vec2

class Amble: TaskComponent() {
    var nextPosition = vec2()
    val path = Queue<Vector2>()
    var needsNew = true
    init {
        coolDownRange = (30f..180f)
        path.clear()
        nextPosition = vec2()
        needsNew = true
        reset()
    }

    override fun toString(): String {
        return "amble"
    }
}

