package injection

import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.ControlMapper
import ecs.systems.*
import ecs.systems.ai.*
import factories.player
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
import physics.ContactManager
import tru.FirstScreen
import ui.IUserInterface
import ui.UserInterface

object Context {
    val context = Context()

    init {
        buildContext()
    }

    inline fun <reified T>inject():T {
        return context.inject()
    }



    private fun buildContext() {
        context.register {
            bindSingleton(ControlMapper())
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(OrthographicCamera())
            bindSingleton<IUserInterface>(UserInterface(inject<PolygonSpriteBatch>() as Batch))
            bindSingleton(
                ExtendViewport(
                    FirstScreen.GAMEWIDTH,
                    FirstScreen.GAMEHEIGHT,
                    inject<OrthographicCamera>() as Camera
                ))
            bindSingleton(createWorld().apply {
                setContactListener(ContactManager())
            })
            bindSingleton(AudioPlayer())
            bindSingleton(getEngine())
            bindSingleton(player())
        }
    }

    private fun getEngine(): Engine {
        return PooledEngine().apply {
            addSystem(PhysicsSystem(inject()))
       //     addSystem(PhysicsDebugRendererSystem(inject(), inject()))
            addSystem(CameraUpdateSystem())
            addSystem(PlayerControlSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(EnterVehicleSystem())
            addSystem(ExitVehicleSystem())
            addSystem(VehicleControlSystem())
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
            addSystem(EnemyDebugRenderSystem(true, true))
            addSystem(RenderSystem(inject<PolygonSpriteBatch>() as Batch))
            addSystem(RenderMiniMapSystem())
        }
    }
}