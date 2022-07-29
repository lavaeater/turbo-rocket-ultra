package screens

import com.badlogic.gdx.Input

class CommandMap(val name: String) {
    private val commands = mutableMapOf<Int, (KeyPress) -> Unit>()
    private val descriptions = mutableMapOf<Int, String>()
    fun setUp(keycode: Int, description: String, up: () -> Unit) {
        setBoth(keycode, description, up) {}
    }

    fun setDown(keycode: Int, description: String, down: () -> Unit) {
        setBoth(keycode, description, {}, down)
    }

    fun setBoth(keycode: Int, description: String, up: () -> Unit, down: () -> Unit) {
        descriptions[keycode] = description
        commands[keycode] = {
            when (it) {
                KeyPress.Down -> down()
                KeyPress.Up -> up()
            }
        }
    }

    fun execute(keycode: Int, keyPress: KeyPress): Boolean {
        if (commands.containsKey(keycode)) {
            commands[keycode]!!(keyPress)
            return true
        }
        return false
    }

    override fun toString(): String {
        return descriptions.map { "${Input.Keys.toString(it.key)}: ${it.value}" }.joinToString("\n")
    }
}