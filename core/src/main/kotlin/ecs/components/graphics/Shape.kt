package ecs.components.graphics

sealed class Shape {
    object Rectangle: Shape()
    object Dot: Shape()
}