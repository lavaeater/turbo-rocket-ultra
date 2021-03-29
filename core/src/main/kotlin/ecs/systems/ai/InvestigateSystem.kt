package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.ai.Investigate
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class InvestigateSystem : IteratingSystem(allOf(Investigate::class, EnemyComponent::class, NoticedSomething::class).get()) {

    private val mapper = mapperFor<Investigate>()
    private val nMapper = mapperFor<NoticedSomething>()
    private val eMapper = mapperFor<EnemyComponent>()
    private val tMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = mapper.get(entity)

        if (component.status == Task.Status.RUNNING) {
            val notice = nMapper[entity]
            val transformComponent = tMapper[entity]

            if(transformComponent.position.dst(notice.noticedWhere) > 2f) {
                val directionVector = notice.noticedWhere.cpy().sub(transformComponent.position).nor()
                eMapper[entity].directionVector.set(directionVector)
                component.coolDown -= deltaTime
            } else {
                eMapper[entity].directionVector.set(Vector2.Zero)
                entity.remove(NoticedSomething::class.java)
            }
            if(component.coolDown <= 0f)
                component.status = Task.Status.FAILED
        }
    }
}