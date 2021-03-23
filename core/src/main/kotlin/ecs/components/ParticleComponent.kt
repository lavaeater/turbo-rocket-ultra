package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color

class ParticleComponent(var life: Float, val color: Color) : Component