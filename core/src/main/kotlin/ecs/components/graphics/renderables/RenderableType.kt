package ecs.components.graphics.renderables

sealed class RenderableType {
    object AnimatedCharacterSprite: RenderableType()
    object Texture: RenderableType()
    object Box: RenderableType()
    object NoOp : RenderableType()
    object Splatter : RenderableType()
    object ListOfRenderables : RenderableType()
}