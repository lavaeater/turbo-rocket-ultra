package ecs.components.graphics

import com.badlogic.ashley.core.Component
import ecs.components.graphics.renderables.NoOpRenderable
import ecs.components.graphics.renderables.Renderable

class RenderableComponent : Component {
    var renderable: Renderable = NoOpRenderable()
}