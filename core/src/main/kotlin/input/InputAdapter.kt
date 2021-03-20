package input

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.ashley.get
import ktx.ashley.hasNot
import ktx.ashley.mapperFor
import ktx.math.vec3

class InputAdapter(
    private var currentControlMapper: ControlMapper = inject(),
    private val camera : OrthographicCamera = inject()) :
    KtxInputAdapter {

    private val controllers by lazy { Controllers.getControllers() }
    private val player: Player by lazy { inject()}
    private val transform: TransformComponent by lazy { player.entity[mapperFor()]!!}

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> currentControlMapper.thrust = 1f
            Input.Keys.S -> currentControlMapper.thrust = -1f
            Input.Keys.A -> currentControlMapper.turning = -1f
            Input.Keys.D -> currentControlMapper.turning = 1f
            Input.Keys.SPACE -> currentControlMapper.firing = true
            else -> return false
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
            else -> return false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if(button == Input.Buttons.LEFT) {
            currentControlMapper.firing = true
            true
        } else false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if(!currentControlMapper.firing) {
            currentControlMapper.firing = true
        }
        //Set angle
        setAimVector(currentControlMapper.aimVector, screenX, screenY)

        return true
    }

    private val currentMousePosition = vec3()
    private fun setAimVector(aimVector: Vector2, screenX: Int, screenY: Int) {
        currentMousePosition.set(screenX.toFloat(), screenY.toFloat(), 0f)

        camera.unproject(currentMousePosition)

        currentControlMapper.mousePosition.set(currentMousePosition.x, currentMousePosition.y)
        aimVector.set(currentMousePosition.x, currentMousePosition.y).sub(transform.position).nor()
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if(button == Input.Buttons.LEFT) {
            currentControlMapper.firing = false
            true
        }
        else false
    }
}