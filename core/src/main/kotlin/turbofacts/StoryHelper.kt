package turbofacts

import audio.AudioPlayer
import factories.factsOfTheWorld
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import messaging.Message
import messaging.MessageHandler
import statemachine.StateMachine

object StoryHelper {
    val factsOfTheWorld by lazy { factsOfTheWorld() }
    private val gameStateMachine by lazy { inject<StateMachine<GameState, GameEvent>>() }
    private val messageHandler by lazy { inject<MessageHandler>() }
    private fun levelStartFacts() {
        factsOfTheWorld.silent {
            setBooleanFact(false, Factoids.BossIsDead)
            setBooleanFact(false, Factoids.AllObjectivesAreTouched)
            setBooleanFact(false, Factoids.ShowEnemyKillCount)
            setBooleanFact(false, Factoids.LevelComplete)
            setBooleanFact(false, Factoids.LevelFailed)
            setBooleanFact(false, Factoids.BossIsDead)
            setIntFact(0, Factoids.EnemyKillCount)
            setIntFact(3, Factoids.TargetEnemyKillCount)
            setBooleanFact(true, Factoids.ShowEnemyKillCount)
            setBooleanFact(false, Factoids.AcceleratingSpawns)
            setBooleanFact(false, Factoids.LevelStarted)
            setBooleanFact(false, Factoids.GotoNextLevel)
        }
    }

    val basicStory by lazy {

        story {
            name = "Touch All Objectives and Kill the Boss"
            initializer = {
                levelStartFacts()
            }
            rule {
                isTrue(Factoids.LevelStarted)
                isTrue(Factoids.BossIsDead)
                isTrue(Factoids.AllObjectivesAreTouched)
            }
            consequence = {
                factsOfTheWorld.setBooleanFact(true, Factoids.LevelComplete)
            }
        }
    }

    val levelCompleteStory by lazy {
        story {
            name = "Level Complete Story"
            rule {
                name = "Level Complete Rules"
                isTrue(Factoids.LevelComplete)
                isTrue(Factoids.LevelStarted)
                consequence = {
                    factsOfTheWorld.setBooleanFact(true, Factoids.GotoNextLevel)
                    gameStateMachine.acceptEvent(GameEvent.PausedGame)
                    messageHandler.sendMessage(Message.LevelComplete(factsOfTheWorld.getString(Factoids.MapSuccessMessage)))
                }
            }
        }
    }

    val levelFailedStory by lazy {
        story {
            name = "Level Failed Story"
            rule {
                name = "General Game Over Rules"
                intEquals(0, Factoids.LivingPlayerCount)
                isTrue(Factoids.LevelStarted)
                consequence = {
                    factsOfTheWorld.setBooleanFact(true, Factoids.LevelFailed)
                    gameStateMachine.acceptEvent(GameEvent.PausedGame)
                    messageHandler.sendMessage(Message.LevelFailed(factsOfTheWorld.getString(Factoids.MapFailMessage)))
                }
            }
        }
    }

    val levelStartStory by lazy {
        story {
            name = "Game Start Story"
            rule {
                name = "Level Start Rules"
                isFalse(Factoids.LevelStarted)
                consequence = {
                        factsOfTheWorld.setTrue(Factoids.LevelStarted)
                        factsOfTheWorld.setFalse(Factoids.LevelComplete)
                        factsOfTheWorld.setFalse(Factoids.LevelFailed)
                        gameStateMachine.acceptEvent(GameEvent.PausedGame)
                        messageHandler.sendMessage(Message.LevelStarting(factsOfTheWorld.getString(Factoids.MapStartMessage)))
                }
            }
        }
    }

    val enemyKillCountStory by lazy {

        story {
            name = "Touch All Objectives and Kill the Boss"
            initializer = {
                levelStartFacts()
                factsOfTheWorld.silent {
                    setTrue(Factoids.AcceleratingSpawns)
                    setFloatFact(1.25f, Factoids.AcceleratingSpawnsFactor)
                }
            }
            rule {
                name = "Check If Work is Done"
                intMoreThan(Factoids.EnemyKillCount, Factoids.TargetEnemyKillCount)
                consequence = {
                        factsOfTheWorld.setTrue(Factoids.LevelComplete)
                }
            }
        }
    }
    val baseStories by lazy {
        listOf(levelStartStory, levelFailedStory, levelCompleteStory).toTypedArray()
    }
}