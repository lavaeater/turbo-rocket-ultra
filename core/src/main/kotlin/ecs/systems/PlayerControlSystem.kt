package ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Logger
import ecs.components.ControlComponent
import ecs.components.TransformComponent
import factories.shot
import gamestate.Player
import injection.Context.inject
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2

class PlayerControlSystem(
    private val rof: Float = 0.1f,
    private val controlComponent: ControlComponent = inject()): EntitySystem() {
    private var lastShot = 0f
    private val player: Player by lazy { inject()  }

    init {
        Gdx.app.logLevel = Logger.DEBUG
    }

    override fun update(deltaTime: Float) {
        handleShooting(deltaTime)
        handleInput()
    }


    private val transformMapper = mapperFor<TransformComponent>()

    // Move to a system
    private fun handleShooting(delta: Float) {
        if (controlComponent.firing) {
            lastShot += delta
            if (lastShot > rof) {
                lastShot = 0f
                shot(player.entity[transformMapper]!!.position.cpy().add(controlComponent.aimVector.cpy().scl(3f)),
                    controlComponent.aimVector.cpy())
            }
        }
    }

    private fun handleInput() {
        if (controlComponent.wheelAngle != 0f) {
            player.body.applyTorque(50f * controlComponent.wheelAngle, true)
        }

        val forceVector = vec2(MathUtils.cos(player.body.angle), MathUtils.sin(player.body.angle)).rotate90(1)

        if (controlComponent.thrust > 0f)
            player.body.applyForceToCenter(forceVector.scl(200f), true)
    }
}