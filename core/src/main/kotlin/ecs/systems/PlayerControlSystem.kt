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


/**
 * Let's think about controls.
 *
 * We will have 2-8 player support. Every player must be able to control both
 * a character or a vehicle - or some other future concept.
 *
 * We do not need to support more than one player from the get-go,
 * we just need to be able to support different steering regimes
 *
 * So, we have this fancy class called "ShipControl" that is manipulated by
 * the input manager. But the ship control actually does not contain any
 * controlling - the controlling is done by this little baby, the
 * "ControlSystem". It manipulates the body according to the values
 * present in the shipcontrol class.
 *
 * So, how do we tell the game that a player is ON a vehicle?
 */

/***
 * Change this to iteratingsystem.... later.
 *
 * That takes a player component and physicscomponents etc.
 *
 * Or rather, a playercontrolcomponent that we can add / remove as
 * we please.
 *
 * This playercontrolcomponent can then be switched to the CAR entity
 * so we control that entity instead of the player entity. Fucking smashing stuff.
 */
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