package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class TransformComponent(val position: Vector2 = vec2(0f, 0f), var rotation: Float = 0f) : Component