package dependencies

import audio.AudioPlayer
import box2dLight.RayHandler
import box2dLight.RayHandlerOptions
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.PooledEngine
import components.AiComponent
import components.ai.BehaviorComponent
import ktx.ashley.allOf
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.BloomEffect
import com.crashinvaders.vfx.effects.CrtEffect
import com.crashinvaders.vfx.effects.OldTvEffect
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.GUIConsole
import systems.UpdateActionsSystem
import systems.UpdateMemorySystem
import systems.UtilityAiSystem
import messaging.IMessage
import messaging.IMessageReceiver
import messaging.MessageHandler
import turbofacts.TurboFactsOfTheWorld
import turbofacts.TurboStoryManager
import systems.AnchorPointTransformationSystem
import systems.BodyDestroyerSystem
import systems.CharacterWalkAndShootDirectionSystem
import systems.PhysicsSystem
import systems.ai.towers.TowerShootSystem
import systems.ai.towers.TowerTargetFinderSystem
import systems.facts.FactSystem
import systems.facts.PerimeterObjectiveSystem
import systems.fx.DelayedEntityCreationSystem
import systems.graphics.GameConstants.GAME_HEIGHT
import systems.graphics.GameConstants.GAME_WIDTH
import systems.input.GamepadInputSystem
import systems.input.InputActionHandler
import systems.input.KeyboardInputSystem
import systems.intent.CalculatePositionSystem
import systems.intent.CalculateRotationSystem
import systems.intent.IntentionSystem
import systems.intent.RunFunctionsSystem
import systems.pickups.LootDropSystem
import ktx.box2d.createWorld
import map.grid.GridMapManager
import messaging.Message
import physics.BodyEntityMapper
import physics.ContactManager
import systems.ai.AudioSystem
import systems.ai.BurningSystem
import systems.ai.DestroyAfterCooldownSystem
import systems.ai.UpdateTimePieceSystem
import systems.enemy.EnemyAnimationSystem
import systems.enemy.EnemyDeathSystem
import systems.enemy.EnemyHearsShotsSystem
import systems.enemy.EnemyMovementSystem
import systems.enemy.EnemyOptimizerSystem
import systems.enemy.EnemySpawnSystem
import systems.enemy.GibSystem
import systems.graphics.AimingAidSystem
import systems.graphics.AnimationSystem
import systems.graphics.CameraUpdateSystem
import systems.graphics.FrustumCullingSystem
import systems.graphics.RenderMiniMapSystem
import systems.graphics.RenderSystem
import systems.player.ComplexActionSystem
import systems.player.PlayerContextActionSystem
import systems.player.PlayerDeathSystem
import systems.player.PlayerFlashlightSystem
import systems.player.PlayerHasBeenHereSystem
import systems.player.PlayerMoveSystem
import systems.player.PlayerShootingSystem
import systems.player.UpdatePlayerStatsSystem
import systems.player.WeaponReloadSystem
import systems.ai.ConversationTriggerSystem
import ui.Hud
import ui.IUserInterface

object Context : InjectionContext() {
    private val shapeDrawerRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    fun initializeContext() {
        buildContext {
            bindSingleton(BodyEntityMapper())
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(ShapeDrawer(inject<PolygonSpriteBatch>() as Batch, shapeDrawerRegion))
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
            val rayHandlerOptions = RayHandlerOptions().apply {
                diffuse = false
                gammaCorrection = true
                pseudo3d = true
            }
            bindSingleton(RayHandler(inject(), rayHandlerOptions))
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
            bindSingleton(story.conversation.ConversationManager(inject()))
            bindSingleton(getEngine())
            bindSingleton(listOf(BloomEffect(), CrtEffect(), OldTvEffect()))
            bindSingleton(VfxManager(Pixmap.Format.RGBA8888))
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addEntityListener(
                allOf(AiComponent::class, BehaviorComponent::class).get(),
                object : EntityListener {
                    override fun entityAdded(entity: Entity) =
                        error("Entity has both AiComponent (utility AI) and BehaviorComponent (behavior tree) — each entity must use exactly one AI system")
                    override fun entityRemoved(entity: Entity) {}
                }
            )
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
            addSystem(EnemyDeathSystem(audioPlayer = inject(), factsOfTheWorld = inject<TurboFactsOfTheWorld>()))
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
                    1,
                    false
                )
            )

            addSystem(RenderMiniMapSystem(2))
            //We add this here now to make sure it is run AFTER the rendercycle
//            addSystem(BehaviorTreeSystem(4))
            addSystem(UtilityAiSystem())
            addSystem(ConversationTriggerSystem())
            addSystem(UpdateActionsSystem())
            addSystem(UpdateMemorySystem())
            addSystem(PlayerFlashlightSystem())
            addSystem(PlayerContextActionSystem())
            addSystem(DelayedEntityCreationSystem())
            addSystem(LootDropSystem())
            addSystem(AimingAidSystem(renderRedDot = false))
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