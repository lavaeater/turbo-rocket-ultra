package ecs.components.ai.boss

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.math.vec2
import physics.getComponent
import physics.has


/**
 * Either we can or we can't do it like this. Would be cool
 * if we could, but perhaps components are indeed their lowest class?
 */
class RushPlayer :TaskComponent() {
    val rushPoint = vec2()
    var previousDistance = 0f

    override fun reset() {
        super.reset()
        previousDistance = 0f
        rushPoint.setZero()
    }

}