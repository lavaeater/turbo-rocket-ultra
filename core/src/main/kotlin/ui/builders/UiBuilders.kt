package ui.builders

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import tru.Builder
import ui.simple.ContainerBaseActor
import ui.simple.SimpleActor
import ui.simple.SpacedContainer
import ui.simple.TextActor

fun textLabel(text: String = "", position: Vector2 = vec2()) = TextActorBuilder(text, position).build()
fun rootSpacedContainer(block: SpacedContainerBuilder.() -> Unit) = SpacedContainerBuilder().apply { root = true }.apply(block).build()
fun ContainerBuilder.textLabel(text: String = "", position: Vector2 = vec2()) = this.addChild(ui.builders.textLabel(text, position))

class TextActorBuilder(var text: String = "", var position: Vector2 = vec2()) : Builder<SimpleActor> {
    override fun build(): TextActor = TextActor(text, position)
}

abstract class ContainerBuilder: Builder<ContainerBaseActor> {
    val childActors = mutableListOf<SimpleActor>()
    fun addChild(actor: SimpleActor) {
        childActors.add(actor)
    }
}

class SpacedContainerBuilder : ContainerBuilder() {
    var offset = vec2()
    var position = vec2()
    var root = false

    override fun build(): SpacedContainer = SpacedContainer(offset, position, root).apply { children.addAll(childActors) }
}