package input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Logger
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.ashley.get
import ktx.ashley.hasNot
import ktx.ashley.mapperFor
import ktx.math.vec2
import ktx.math.vec3

class InputAdapter(
    private var currentControlMapper: ControlMapper = inject(),
    private val camera : OrthographicCamera = inject()) :
    KtxInputAdapter {

    init {
        Gdx.app.logLevel = Logger.DEBUG
    }

    private val player: Player by lazy { inject()}
    private val transform: TransformComponent by lazy { player.entity[mapperFor()]!!}

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> currentControlMapper.thrust = 1f
            Input.Keys.S -> currentControlMapper.thrust = -1f
            Input.Keys.A -> currentControlMapper.turning = -1f
            Input.Keys.D -> currentControlMapper.turning = 1f
            Input.Keys.SPACE -> currentControlMapper.firing = true
        }
        return true
    }

    private fun toggleVehicle() {
        if (player.entity.hasNot(mapperFor<IsInVehicleComponent>())) {
            player.entity.add(EnterVehicleComponent())
        } else {
            player.entity.add(LeaveVehicleComponent())
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {

            Input.Keys.W -> currentControlMapper.thrust = 0f
            Input.Keys.S -> currentControlMapper.thrust = 0f
            Input.Keys.A -> currentControlMapper.turning = 0f
            Input.Keys.D -> currentControlMapper.turning = 0f
            Input.Keys.J -> toggleVehicle()
            Input.Keys.SPACE -> currentControlMapper.firing = false
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    private fun getAimVector(screenX: Int, screenY: Int): Vector2 {
        val pos = vec3(screenX.toFloat(), screenY.toFloat(), 0f)

        camera.unproject(pos)

        currentControlMapper.mousePosition.set(pos.x, pos.y)

        return vec2(pos.x, pos.y).sub(transform.position).nor()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(button == Input.Buttons.LEFT) {
            currentControlMapper.firing = true
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if(!currentControlMapper.firing) {
            currentControlMapper.firing = true
        }
        //Set angle
        currentControlMapper.aimVector.set(getAimVector(screenX, screenY))

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(button == Input.Buttons.LEFT) {
            currentControlMapper.firing = false
        }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

}