package ecs.components.graphics

sealed class RenderableType {
    object Effect: RenderableType()
    object TextureRegion: RenderableType()
}