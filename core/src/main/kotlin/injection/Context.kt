package injection

import audio.AudioPlayer
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.BloomEffect
import com.crashinvaders.vfx.effects.CrtEffect
import com.crashinvaders.vfx.effects.OldTvEffect
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.GUIConsole
import eater.injection.InjectionContext
import eater.messaging.IMessage
import eater.messaging.IMessageReceiver
import ecs.systems.AnchorPointTransformationSystem
import ecs.systems.BodyDestroyerSystem
import ecs.systems.CharacterWalkAndShootDirectionSystem
import ecs.systems.PhysicsSystem
import ecs.systems.ai.*
import ecs.systems.ai.towers.TowerShootSystem
import ecs.systems.ai.towers.TowerTargetFinderSystem
import ecs.systems.enemy.*
import ecs.systems.facts.FactSystem
import ecs.systems.facts.PerimeterObjectiveSystem
import ecs.systems.fx.DelayedEntityCreationSystem
import ecs.systems.graphics.*
import ecs.systems.graphics.GameConstants.GAME_HEIGHT
import ecs.systems.graphics.GameConstants.GAME_WIDTH
import ecs.systems.input.InputActionHandler
import ecs.systems.input.GamepadInputSystem
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.intent.CalculatePositionSystem
import ecs.systems.intent.CalculateRotationSystem
import ecs.systems.intent.IntentionSystem
import ecs.systems.intent.RunFunctionsSystem
import ecs.systems.pickups.LootDropSystem
import ecs.systems.player.*
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
import map.grid.GridMapManager
import messaging.Message
import eater.messaging.MessageHandler
import physics.ContactManager
import eater.turbofacts.TurboFactsOfTheWorld
import eater.turbofacts.TurboStoryManager
import ui.Hud
import ui.IUserInterface

object Context {
    val context = Context()

    init {
        buildContext()
    }

    inline fun <reified T> inject(): T {
        return context.inject()
    }

    private fun buildContext() {
        context.register {
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(InputActionHandler())
            bindSingleton(OrthographicCamera())
            bindSingleton<IUserInterface> { Hud(inject<PolygonSpriteBatch>() as Batch, false) }
            bindSingleton(
                ExtendViewport(
                    GAME_WIDTH,
                    GAME_HEIGHT,
                    inject<OrthographicCamera>() as Camera
                )
            )
            bindSingleton(GUIConsole().apply {
                setCommandExecutor(CommandExecutor())
                displayKeyID = Input.Keys.U
            })
            bindSingleton(ContactManager())
            bindSingleton(createWorld().apply {
                setContactListener(inject<ContactManager>())
            })
            bindSingleton(AudioPlayer())
            bindSingleton(GridMapManager())
            bindSingleton(RayHandler(inject(), 500, 500))
            bindSingleton(MessageHandler())
            bindSingleton(TurboStoryManager().apply {
                inject<MessageHandler>().apply {
                    this.addReceiver(object : IMessageReceiver {
                        override val messageTypes = setOf(Message.FactUpdated::class)

                        override fun receiveMessage(message: IMessage) {
                            when (message) {
                                is Message.FactUpdated -> needsChecking = true
                                else -> {}
                            }
                        }

                    })
                }
            })
            bindSingleton(TurboFactsOfTheWorld { key -> inject<MessageHandler>().sendMessage(Message.FactUpdated(key)) })
            bindSingleton(getEngine())
            bindSingleton(listOf(BloomEffect(), CrtEffect(), OldTvEffect()))
            bindSingleton(VfxManager(Pixmap.Format.RGBA8888))
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(UpdateTimePieceSystem())
            addSystem(PhysicsSystem(0))
            addSystem(CameraUpdateSystem(inject(), inject()))
            addSystem(PlayerMoveSystem())
            addSystem(PlayerHasBeenHereSystem())
            addSystem(KeyboardInputSystem())
            addSystem(GamepadInputSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(CharacterWalkAndShootDirectionSystem())
            addSystem(PlayerShootingSystem(inject()))
            addSystem(EnemyDeathSystem(audioPlayer = inject(), factsOfTheWorld = inject()))
            addSystem(EnemyMovementSystem(true))
            addSystem(PerimeterObjectiveSystem())
            addSystem(EnemyAnimationSystem())
            addSystem(EnemyHearsShotsSystem())
            addSystem(AudioSystem())
            // Ai Systems End
            //Burning
            addSystem(BurningSystem())
            addSystem(DestroyAfterCooldownSystem())
            //Burning End
            addSystem(PlayerDeathSystem())
            addSystem(EnemySpawnSystem())
            addSystem(EnemyOptimizerSystem())
            addSystem(TowerTargetFinderSystem())
            addSystem(TowerShootSystem())
            addSystem(AnimationSystem())
            addSystem(WeaponReloadSystem())
            addSystem(UpdatePlayerStatsSystem())
            addSystem(
                RenderSystem(
                    inject<PolygonSpriteBatch>() as Batch,
                    false,
                    inject(),
                    inject(),
                    inject<ExtendViewport>(),
                    false,
                    1,
                    false
                )
            )

            addSystem(RenderMiniMapSystem(2))
            //We add this here now to make sure it is run AFTER the rendercycle
            addSystem(BehaviorTreeSystem(4))
            addSystem(PlayerFlashlightSystem())
            addSystem(PlayerContextActionSystem())
            addSystem(DelayedEntityCreationSystem())
            addSystem(LootDropSystem())
            addSystem(AimingAidSystem(debug = false, renderRedDot = false))
            addSystem(GibSystem())
            addSystem(FactSystem())
            addSystem(FrustumCullingSystem())
            addSystem(AnchorPointTransformationSystem(false))
            addSystem(ComplexActionSystem())
            addSystem(IntentionSystem())
            addSystem(CalculatePositionSystem())
            addSystem(CalculateRotationSystem())
            addSystem(RunFunctionsSystem())
        }
    }
}