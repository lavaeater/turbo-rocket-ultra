package ecs.components

import com.badlogic.ashley.core.Component

class VehicleComponent(
    val maxForwardSpeed: Float = 100f,
    val maxBackwardSpeed: Float = 20f,
    val maxDriveForce: Float = 150f
) : Component