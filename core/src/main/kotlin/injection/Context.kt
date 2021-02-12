package injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.ControlComponent
import ecs.systems.*
import factories.player
import ktx.box2d.createWorld
import ktx.inject.Context
import ktx.inject.register
import physics.ContactManager
import tru.FirstScreen


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
            bindSingleton(ControlComponent())
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(OrthographicCamera())
            //bindSingleton<IUserInterface>(UserInterface(inject<PolygonSpriteBatch>() as Batch, false))
            bindSingleton(
                ExtendViewport(
                    FirstScreen.GAMEWIDTH,
                    FirstScreen.GAMEHEIGHT,
                    inject<OrthographicCamera>() as Camera
                ))
            bindSingleton(createWorld().apply {
                setContactListener(ContactManager())
            })
            bindSingleton(getEngine())
            bindSingleton(player())
        }
    }

    private fun getEngine(): Engine {
        return Engine().apply {
            addSystem(PhysicsSystem(
                inject())) //box2dWorld
            addSystem(PhysicsDebugRendererSystem(
                inject(), //Box2dWorld
                inject())) //Camera
            addSystem(CameraUpdateSystem())
            addSystem(PlayerControlSystem())
            addSystem(BodyDestroyerSystem(inject())) //world
            addSystem(EnterVehicleSystem())
            addSystem(VehicleControlSystem())
            //addSystem(AimDebugSystem())
        }
    }
}