package story

import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import statemachine.StateMachine
import story.fact.Facts


object StoryHelper {
    val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }
    val gameStateMachine by lazy { inject<StateMachine<GameState, GameEvent>>() }
    val levelCompleteStory by lazy {
        story {
            name = "Touch All Objectives and Kill the Boss"
            neverEnding = true
            initializer = {
                factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
                factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
            }
            storyBeat {
                name = "Check If Work is Done"
                booleanCriteria(Facts.BossIsDead, true)
                booleanCriteria(Facts.AllObjectivesAreTouched, true)
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, true)
                    }
                }
            }
        }
    }
}