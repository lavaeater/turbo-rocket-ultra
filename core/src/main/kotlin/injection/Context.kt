package injection

import audio.AudioPlayer
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.systems.*
import ecs.systems.ai.*
import ecs.systems.ai.towers.TowerShootSystem
import ecs.systems.ai.towers.TowerTargetFinderSystem
import ecs.systems.enemy.*
import ecs.systems.fx.BloodSplatterEffectRenderSystem
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.fx.RenderBox2dLightSystem
import ecs.systems.graphics.*
import ecs.systems.input.GamepadInputSystem
import ecs.systems.pickups.LootDropSystem
import ecs.systems.player.*
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
import map.grid.GridMapManager
import physics.ContactManager
import screens.GameScreen
import ui.IUserInterface
import ui.UserInterface

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
            bindSingleton(OrthographicCamera())
            bind<IUserInterface> { UserInterface(inject<PolygonSpriteBatch>() as Batch)}
            bindSingleton(
                ExtendViewport(
                    GameScreen.GAMEWIDTH,
                    GameScreen.GAMEHEIGHT,
                    inject<OrthographicCamera>() as Camera
                )
            )
            bindSingleton(createWorld().apply {
                setContactListener(ContactManager())
            })
            bindSingleton(AudioPlayer())
            bindSingleton(GridMapManager())
            bindSingleton(RayHandler(inject()))
            bindSingleton(getEngine())
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(PhysicsSystem(inject()))
            //addSystem(PhysicsDebugRendererSystem(inject(), inject()))
            addSystem(CameraUpdateSystem())
            addSystem(PlayerMoveSystem())
            addSystem(PlayerBuildModeSystem())
            addSystem(KeyboardInputSystem())
            addSystem(GamepadInputSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(CharacterWalkAndShootDirectionSystem())
            addSystem(PlayerShootingSystem(inject()))
            addSystem(EnemyDeathSystem())
            addSystem(EnemyMovementSystem())
            addSystem(AmblingSystem())
            addSystem(BehaviorTreeSystem())
            addSystem(ChasePlayerSystem())
            addSystem(SeekingPlayerSystem())
            addSystem(AttackPlayerSystem())
            addSystem(EnemyDirectionSystem())
            addSystem(EnemyHearsShotsSystem())
            addSystem(InvestigateSystem())
            addSystem(PlayerDeathSystem())
            addSystem(EnemySpawnSystem())
            addSystem(EnemyOptimizerSystem())
            addSystem(TowerTargetFinderSystem())
            addSystem(TowerShootSystem())
            addSystem(AnimationSystem())
            addSystem(WeaponUpdateSystem())
            addSystem(WeaponChangeAndReloadSystem())
            addSystem(UpdatePlayerStatsSystem())
            addSystem(RenderMapSystem(inject<PolygonSpriteBatch>() as Batch, inject<OrthographicCamera>() as Camera, inject()))
            addSystem(SimpleRenderSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(RenderUserInterfaceSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(RenderMiniMapSystem())
            addSystem(PlayerFlashlightSystem())
            addSystem(WeaponLaserSystem())
            addSystem(RenderBox2dLightSystem(inject(), inject()))
            addSystem(BloodSplatterEffectRenderSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(LootDropSystem())
        }
    }
}