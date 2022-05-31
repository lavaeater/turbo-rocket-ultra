package injection

import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.systems.BodyDestroyerSystem
import ecs.systems.CharacterWalkAndShootDirectionSystem
import ecs.systems.PhysicsSystem
import ecs.systems.ai.*
import ecs.systems.ai.towers.TowerShootSystem
import ecs.systems.ai.towers.TowerTargetFinderSystem
import ecs.systems.enemy.*
import ecs.systems.fx.AddSplatterSystem
import ecs.systems.fx.SplatterRemovalSystem
import ecs.systems.graphics.*
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.player.PlayerDeathSystem
import ecs.systems.player.PlayerMoveSystem
import ecs.systems.player.PlayerShootingSystem
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
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
            bindSingleton(getEngine())
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(PhysicsSystem(inject()))
            addSystem(CameraUpdateSystem())
            addSystem(PlayerMoveSystem())
            addSystem(KeyboardInputSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(CharacterWalkAndShootDirectionSystem())
            addSystem(ShootDebugRenderSystem())
            addSystem(PlayerShootingSystem(inject()))
            addSystem(EnemyDeathSystem())
            addSystem(EnemyMovementSystem())
            addSystem(AmblingSystem())
            addSystem(BehaviorTreeSystem())
            addSystem(ChasePlayerSystem())
            addSystem(SeekingPlayerSystem())
            addSystem(AttackPlayerSystem())
            addSystem(EnemyDirectionSystem())
            addSystem(AddSplatterSystem())
            addSystem(SplatterRemovalSystem())
            addSystem(EnemyHearsShotsSystem())
            addSystem(InvestigateSystem())
            addSystem(EnemyDebugRenderSystem(false, false))
            addSystem(PlayerDeathSystem())
            addSystem(EnemySpawnSystem())
            addSystem(EnemyOptimizerSystem())
            addSystem(TowerDebugSystem())
            addSystem(TowerTargetFinderSystem())
            addSystem(TowerShootSystem())
            addSystem(RenderSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(RenderMiniMapSystem())
        }
    }
}