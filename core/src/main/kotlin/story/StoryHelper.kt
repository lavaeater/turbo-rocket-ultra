package story

import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import messaging.Message
import messaging.MessageHandler
import statemachine.StateMachine
import story.fact.Facts


object StoryHelper {
    val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }
    val gameStateMachine by lazy { inject<StateMachine<GameState, GameEvent>>() }
    val messageHandler by lazy { inject<MessageHandler>()}


    private fun levelStartFacts() {
        factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
        factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
        factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, false)
        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
        factsOfTheWorld.stateBoolFact(Facts.LevelFailed, false)
        factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
        factsOfTheWorld.stateIntFact(Facts.EnemyKillCount, 0)
        factsOfTheWorld.stateIntFact(Facts.TargetEnemyKillCount, 50)
        factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, true)
        factsOfTheWorld.stateBoolFact(Facts.AcceleratingSpawns, false)
        factsOfTheWorld.stateBoolFact(Facts.LevelStarted, false)
        factsOfTheWorld.stateBoolFact(Facts.GotoNextLevel, false)
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
                equalsCriterion(Facts.LevelStarted, true)
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

    val levelCompleteStory by lazy {
        story {
            name = "Level Complete Story"
            neverEnding = false
            storyBeat {
                name = "Level Complete Rules"
                equalsCriterion(Facts.LevelComplete, true)
                equalsCriterion(Facts.LevelStarted, true)
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelFailed, true)
                        gameStateMachine.acceptEvent(GameEvent.PausedGame)
                        messageHandler.sendMessage(Message.LevelComplete(factsOfTheWorld.stringForKey(Facts.MapSuccessMessage)))
                    }
                }
            }
        }
    }

    val levelFailedStory by lazy {
        story {
            name = "Level Failed Story"
            neverEnding = false
            storyBeat {
                name = "General Game Over Rules"
                equalsCriterion(Facts.LivingPlayerCount, 0)
                equalsCriterion(Facts.LevelStarted, true)
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                        factsOfTheWorld.stateBoolFact(Facts.LevelFailed, true)
                        gameStateMachine.acceptEvent(GameEvent.PausedGame)
                        messageHandler.sendMessage(Message.LevelFailed(factsOfTheWorld.stringForKey(Facts.MapFailMessage)))
                    }
                }
            }
        }
    }

    val levelStartStory by lazy {
        story {
            name = "Game Start Story"
            neverEnding = false
            storyBeat {
                name = "Level Start Rules"
                equalsCriterion(Facts.LevelStarted, false)
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelStarted, true)
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
                        factsOfTheWorld.stateBoolFact(Facts.LevelFailed, false)
                        gameStateMachine.acceptEvent(GameEvent.PausedGame)
                        messageHandler.sendMessage(Message.LevelStarting(factsOfTheWorld.stringForKey(Facts.MapStartMessage)))
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
            storyBeat {
                name = "Check If Work is Done"
                moreThanCriterion(Facts.EnemyKillCount, factsOfTheWorld.getIntValue(Facts.TargetEnemyKillCount))
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, true)
                    }
                }
            }
        }
    }
}