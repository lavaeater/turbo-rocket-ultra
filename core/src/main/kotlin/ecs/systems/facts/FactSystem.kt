package ecs.systems.facts

import com.badlogic.ashley.systems.IntervalSystem
import ecs.components.enemy.BossComponent
import ecs.components.gameplay.ObjectiveComponent
import factories.factsOfTheWorld
import injection.Context.inject
import ktx.ashley.allOf
import physics.getComponent
import turbofacts.Factoids

/***
 * Takes fact-setting rules and sets facts if these rules are indeed
 * fulfilled.
 */
class FactSystem : IntervalSystem(1f) {
    val rules = mutableListOf<() -> Unit>()
    val factsOfTheWorld by lazy { factsOfTheWorld() }

    init {
        rules.add {
            if (!factsOfTheWorld.getBoolean(Factoids.BossIsDead) && engine.getEntitiesFor(allOf(BossComponent::class).get()).size() == 0) {
                factsOfTheWorld.setBooleanFact(true, Factoids.BossIsDead)
            }
        }
        rules.add {
            if (!factsOfTheWorld.getBoolean(Factoids.AllObjectivesAreTouched) && engine.getEntitiesFor(allOf(ObjectiveComponent::class).get())
                    .all { it.getComponent<ObjectiveComponent>().touched }
            ) {
                factsOfTheWorld.setBooleanFact(true, Factoids.AllObjectivesAreTouched)
            }

        }
    }

    override fun updateInterval() {
        for (rule in rules)
            rule()
    }
}
