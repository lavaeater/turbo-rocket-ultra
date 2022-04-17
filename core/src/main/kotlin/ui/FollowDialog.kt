package ui

import com.badlogic.gdx.scenes.scene2d.ui.Widget
import injection.Context.inject
import ktx.scene2d.KTableWidget
import ktx.scene2d.KWidget
import ktx.scene2d.scene2d
import ktx.scene2d.table

/**
 *
 */
class UiThing(val widget: KTableWidget) {
    fun show() {
        widget.isVisible = true
    }

    fun hide() {
        widget.isVisible = false
    }

    fun update() {
        //Dunno
    }
}

object {
    fun g(): UiThing {
        return getUiThing {
            widget = scene2d.table {

            }
        }
    }
}

fun getUiThing(block: UiThingBuilder.() -> Unit) : UiThing = UiThingBuilder().apply { block }.build()

class UiThingBuilder {
    val stage by lazy { inject<IUserInterface>().stage }
    lateinit var widget: KTableWidget
    fun build() : UiThing {
        stage.addActor(widget)
        return UiThing(widget)
    }
}