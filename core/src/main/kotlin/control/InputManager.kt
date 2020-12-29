package control

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Logger
import ecs.components.TransformComponent
import gamestate.Player
import injection.Context
import ktx.app.KtxInputAdapter
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import ktx.math.vec3

class InputManager(
    private val shipControl: ShipControl = Context.inject(),
    private val camera : OrthographicCamera = Context.inject()) :
    KtxInputAdapter {

    init {
        Gdx.app.logLevel = Logger.DEBUG
    }

    private val player: Player by lazy { Context.inject()}
    private val transform: TransformComponent by lazy { player.entity[mapperFor()]!!}

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> shipControl.throttle(1f)
            Input.Keys.A -> shipControl.turn(-1f)
            Input.Keys.D -> shipControl.turn(1f)
            Input.Keys.SPACE -> shipControl.startFiring()
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> shipControl.throttle(0f)
            Input.Keys.A -> shipControl.turn(0f)
            Input.Keys.D -> shipControl.turn(0f)
            Input.Keys.SPACE -> shipControl.stopFiring()
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    private fun getAimVector(screenX: Int, screenY: Int): Vector2 {
        val pos = vec3(screenX.toFloat(), screenY.toFloat(), 0f)

        camera.unproject(pos)

        shipControl.mouseVector(pos.x, pos.y)

        return vec2(pos.x, pos.y).sub(transform.position).nor()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        /*
        Any button means shooting... no.
         */

        if(button == Input.Buttons.LEFT) {

            //Start firing
            shipControl.startFiring()
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if(!shipControl.firing) {
            shipControl.startFiring()
        }
        //Set angle
        shipControl.aimAt(getAimVector(screenX, screenY))

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(button == Input.Buttons.LEFT) {
            shipControl.stopFiring()
        }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

}