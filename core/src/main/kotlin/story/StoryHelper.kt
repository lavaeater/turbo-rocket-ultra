package story

import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import statemachine.StateMachine
import story.fact.Facts
import ui.IUserInterface


object StoryHelper {
    val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }
    val gameStateMachine by lazy { inject<StateMachine<GameState, GameEvent>>() }
    val basicStory by lazy {
        story {
            name = "Touch All Objectives and Kill the Boss"
            neverEnding = false
            initializer = {
                factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
                factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
                factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, false)
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
    val enemyKillCountStory by lazy {
        story {
            name = "Touch All Objectives and Kill the Boss"
            neverEnding = false
            initializer = {
                factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
                factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
                factsOfTheWorld.stateIntFact(Facts.EnemyKillCount, 0)
                factsOfTheWorld.stateIntFact(Facts.TargetEnemyKillCount, 20)
                factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, true)
                factsOfTheWorld.stateBoolFact(Facts.AcceleratingSpawns, true)

            }
            storyBeat {
                name = "Check If Work is Done"
                moreThanCriterion(Facts.EnemyKillCount, 20)
//                booleanCriteria(Facts.BossIsDead, true)
//                booleanCriteria(Facts.AllObjectivesAreTouched, true)
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, true)
                    }
                }
            }
        }
    }
}