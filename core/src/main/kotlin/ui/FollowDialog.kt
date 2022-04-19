package ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import injection.Context.inject
import ktx.scene2d.KTableWidget
import ktx.scene2d.KWidget
import ktx.scene2d.scene2d
import ktx.scene2d.table

/**
 *
 */
class UiThing(val widget: Actor) {
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

fun getUiThing(block: UiThingBuilder.() -> Unit) : UiThing = UiThingBuilder().apply(block).build()

class UiThingBuilder {
    val stage by lazy { inject<IUserInterface>().stage }
    lateinit var widget: Actor
    fun build() : UiThing {
//        stage.addActor(widget)
        return UiThing(widget)
    }
}