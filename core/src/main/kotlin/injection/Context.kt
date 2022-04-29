package injection

import audio.AudioPlayer
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.*
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.GUIConsole
import ecs.systems.AnchorPointTransformationSystem
import ecs.systems.BodyDestroyerSystem
import ecs.systems.CharacterWalkAndShootDirectionSystem
import ecs.systems.PhysicsSystem
import ecs.systems.ai.*
import ecs.systems.ai.boss.RushPlayerSystem
import ecs.systems.ai.towers.TowerShootSystem
import ecs.systems.ai.towers.TowerTargetFinderSystem
import ecs.systems.enemy.*
import ecs.systems.facts.FactSystem
import ecs.systems.facts.PerimeterObjectiveSystem
import ecs.systems.fx.BloodSplatterEffectRenderSystem
import ecs.systems.fx.DelayedEntityCreationSystem
import ecs.systems.fx.EffectRenderSystem
import ecs.systems.graphics.*
import ecs.systems.graphics.GameConstants.GAMEHEIGHT
import ecs.systems.graphics.GameConstants.GAMEWIDTH
import ecs.systems.input.ActionHandler
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
import physics.ContactManager
import ui.Hud
import ui.IUserInterface
import messaging.MessageHandler
import turbofacts.NewFactsOfTheWorld
import turbofacts.TurboStoryManager

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
            bindSingleton(ActionHandler())
            bindSingleton(OrthographicCamera())
            bindSingleton<IUserInterface> { Hud(inject<PolygonSpriteBatch>() as Batch) }
            bindSingleton(
                ExtendViewport(
                    GAMEWIDTH,
                    GAMEHEIGHT,
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
            bindSingleton(TurboStoryManager())
            bindSingleton(NewFactsOfTheWorld { key -> inject<MessageHandler>().sendMessage(Message.FactUpdated(key)) })
            bindSingleton(getEngine())
            bindSingleton(listOf(BloomEffect(), CrtEffect(), OldTvEffect()))
            bindSingleton(VfxManager(Pixmap.Format.RGBA8888))
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(PhysicsSystem(0))
            addSystem(CameraUpdateSystem(inject(), inject()))
            addSystem(PlayerMoveSystem(25f))
            addSystem(PlayerHasBeenHereSystem())
            addSystem(KeyboardInputSystem())
            addSystem(GamepadInputSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(CharacterWalkAndShootDirectionSystem())
            addSystem(PlayerShootingSystem(inject()))
            addSystem(EnemyDeathSystem(audioPlayer = inject(), factsOfTheWorld = inject()))
            addSystem(EnemyMovementSystem(false))
            addSystem(PerimeterObjectiveSystem())
            // Ai Systems Start
            addSystem(AmblingSystem())
            addSystem(PanicSystem())
            addSystem(BehaviorTreeSystem())
            addSystem(ChasePlayerSystem())
            addSystem(SeekPlayerSystem(false))
            addSystem(AttackPlayerSystem())
            addSystem(EnemyDirectionSystem())
            addSystem(EnemyHearsShotsSystem())
            addSystem(InvestigateSystem())
            addSystem(RushPlayerSystem())
            addSystem(AudioSystem())
            // Ai Systems End
            //Burning
            addSystem(BurningSystem())
            addSystem(DestroyAfterReadingSystem())
            //Burning End
            addSystem(PlayerDeathSystem())
            addSystem(EnemySpawnSystem())
            addSystem(EnemyOptimizerSystem())
            addSystem(TowerTargetFinderSystem())
            addSystem(TowerShootSystem())
            addSystem(AnimationSystem())
            addSystem(WeaponUpdateSystem())
            addSystem(WeaponChangeAndReloadSystem())
            addSystem(UpdatePlayerStatsSystem())
            //addSystem(PhysicsDebugRendererSystem(inject(), inject()))
            addSystem(
                RenderSystem(
                    inject<PolygonSpriteBatch>() as Batch,
                    false,
                    inject(),
                    inject(),
                    inject<ExtendViewport>(),
                    false,
                    1
                )
            )
            addSystem(RenderMiniMapSystem(3))
            addSystem(PlayerFlashlightSystem())
            //lets NOT write debug badges
//            addSystem(AiDebugSystem())
            addSystem(PlayerContextActionSystem())
            addSystem(BloodSplatterEffectRenderSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(DelayedEntityCreationSystem())
            addSystem(EffectRenderSystem(inject<PolygonSpriteBatch>() as Batch, 2))
            addSystem(LootDropSystem())
            addSystem(AimingAidSystem(debug = true, renderRedDot = true))
            addSystem(GibSystem())
            addSystem(FactSystem())
            addSystem(FrustumCullingSystem())
            addSystem(AnchorPointTransformationSystem(true))
            addSystem(ComplexActionSystem())
//            addSystem(BuildSystem(true))
            addSystem(IntentionSystem())
            addSystem(CalculatePositionSystem())
            addSystem(CalculateRotationSystem())
            addSystem(RunFunctionsSystem())

        }
    }
}