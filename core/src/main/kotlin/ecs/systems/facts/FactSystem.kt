package ecs.systems.facts

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem
import ecs.components.enemy.BossComponent
import ecs.components.gameplay.ObjectiveComponent
import injection.Context.inject
import ktx.ashley.allOf
import physics.getComponent
import story.FactsOfTheWorld
import story.fact.Facts

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
