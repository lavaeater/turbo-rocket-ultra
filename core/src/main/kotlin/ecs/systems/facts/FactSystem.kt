package ecs.systems.facts

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.BossComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.PerimeterObjectiveComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import injection.Context.inject
import ktx.ashley.allOf
import physics.getComponent
import physics.objective
import physics.perimeter
import physics.transform
import story.FactsOfTheWorld
import story.fact.Facts

class PerimeterObjectiveSystem: IteratingSystem(allOf(PerimeterObjectiveComponent::class, TransformComponent::class).get())  {
    private val playerEntities get() = engine.getEntitiesFor(allOf(PlayerComponent::class, TransformComponent::class).get())
    private val playerTransforms get() = playerEntities.map { it.transform() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        val perimeter = entity.perimeter()
        val objective = entity.objective()
        objective.touched = playerTransforms.any { it.position.dst(transform.position) < perimeter.distance }
    }

}

/***
 * Takes fact-setting rules and sets facts if these rules are indeed
 * fulfilled.
 */
@OptIn(ExperimentalStdlibApi::class)
class FactSystem : IntervalSystem(1f) {
    val rules = mutableListOf<() -> Unit>()
    val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }

    init {
        rules.add {
            if (engine.getEntitiesFor(allOf(BossComponent::class).get()).size() == 0) {
                factsOfTheWorld.stateBoolFact(Facts.BossIsDead, true)
            }
        }
        rules.add {
            if (engine.getEntitiesFor(allOf(ObjectiveComponent::class).get())
                    .all { it.getComponent<ObjectiveComponent>().touched }
            ) {
                factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, true)
            }

        }
    }

    override fun updateInterval() {
        for (rule in rules)
            rule()
    }
}
