package ecs.components

import com.badlogic.ashley.core.Component

class SeesPlayerComponent(val transformComponent: TransformComponent):Component {
    var shouldUpdateState = true
    val playerPosition get() = transformComponent.position
}