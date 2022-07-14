package ui

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Pool
import eater.injection.InjectionContext.Companion.inject


/**
 *
 */
class UiThingComponent: Component, Pool.Poolable {
    var widget: Actor = Actor()
    fun show() {
        widget.isVisible = true
    }

    fun hide() {
        widget.isVisible = false
    }

    fun update() {
        //Dunno
    }

    override fun reset() {
        widget.isVisible = false
        widget.remove()
        widget = Actor()
    }
}

fun getUiThing(block: UiThingBuilder.() -> Unit) : UiThingComponent = UiThingBuilder().apply(block).build()

class UiThingBuilder {
    val hud by lazy { inject<IUserInterface>()}
    val stage by lazy { hud.stage }
    lateinit var widget: Actor
    fun build() : UiThingComponent {
//        stage.addActor(widget)
        return UiThingComponent().apply { }
    }
}