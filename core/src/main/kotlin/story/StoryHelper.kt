package story

import audio.AudioPlayer
import ecs.components.AudioChannels
import ecs.components.enemy.EnemyComponent
import factories.engine
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import ktx.ashley.allOf
import messaging.Message
import messaging.MessageHandler
import physics.enemy
import statemachine.StateMachine
import story.fact.Facts
import tru.Assets


object StoryHelper {
    val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }
    private val gameStateMachine by lazy { inject<StateMachine<GameState, GameEvent>>() }
    private val messageHandler by lazy { inject<MessageHandler>() }
    private val audioPlayer by lazy { inject<AudioPlayer>() }


    private fun levelStartFacts() {
        factsOfTheWorld.silent {
            it.stateBoolFact(Facts.BossIsDead, false)
            it.stateBoolFact(Facts.AllObjectivesAreTouched, false)
            it.stateBoolFact(Facts.ShowEnemyKillCount, false)
            it.stateBoolFact(Facts.LevelComplete, false)
            it.stateBoolFact(Facts.LevelFailed, false)
            it.stateBoolFact(Facts.BossIsDead, false)
            it.stateIntFact(Facts.EnemyKillCount, 0)
            it.stateIntFact(Facts.TargetEnemyKillCount, 10)
            it.stateBoolFact(Facts.ShowEnemyKillCount, true)
            it.stateBoolFact(Facts.AcceleratingSpawns, false)
            it.stateBoolFact(Facts.LevelStarted, false)
            it.stateBoolFact(Facts.GotoNextLevel, false)
        }
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
                        factsOfTheWorld.stateBoolFact(Facts.GotoNextLevel, true)
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
            neverEnding = true
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
                factsOfTheWorld.silent {
                    it.stateBoolFact(Facts.AcceleratingSpawns, true)
                    it.stateFloatFact(Facts.AcceleratingSpawnsFactor, 1.25f)
                }
            }
            storyBeat {
                name = "Check If Work is Done"
                moreThanWithFunction(Facts.EnemyKillCount) { factsOfTheWorld.getIntValue(Facts.TargetEnemyKillCount) }
                consequence {
                    apply = {
                        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, true)
                    }
                }
            }
        }
    }
    val baseStories by lazy {
        listOf(levelStartStory, levelFailedStory, levelCompleteStory).toTypedArray()
    }
}