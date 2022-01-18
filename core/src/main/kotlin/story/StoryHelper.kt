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

    fun gameOverRules(story: StoryBuilder) {
        story.storyBeat {
            name = "General Game Over Rules"
            equalsCriterion(Facts.LivingPlayerCount, 0)
            consequence {
                apply = {
                    factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                    factsOfTheWorld.stateBoolFact(Facts.LevelFailed, true)
                }
            }
        }
    }

    fun levelStartFacts() {
        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
        factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
        factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
        factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, false)
        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
        factsOfTheWorld.stateBoolFact(Facts.LevelFailed, false)
        factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
        factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
        factsOfTheWorld.stateIntFact(Facts.EnemyKillCount, 0)
        factsOfTheWorld.stateIntFact(Facts.TargetEnemyKillCount, 50)
        factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, true)
        factsOfTheWorld.stateBoolFact(Facts.AcceleratingSpawns, false)
    }
    val basicStory by lazy {
        story {
            name = "Touch All Objectives and Kill the Boss"
            neverEnding = false
            initializer = {
                levelStartFacts()
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
                levelStartFacts()
                factsOfTheWorld.stateBoolFact(Facts.AcceleratingSpawns, true)
                factsOfTheWorld.stateFloatFact(Facts.AcceleratingSpawnsFactor, 1.25f)
            }
            gameOverRules(this)
            storyBeat {
                name = "Check If Work is Done"
                moreThanCriterion(Facts.EnemyKillCount, 200)
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