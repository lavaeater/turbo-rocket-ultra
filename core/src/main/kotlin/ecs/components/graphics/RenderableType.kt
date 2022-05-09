package ecs.components.graphics

sealed class RenderableType {
    object Effect: RenderableType()
    object Sprite: RenderableType()
}