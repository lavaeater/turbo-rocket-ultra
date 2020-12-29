package ecs.components

import com.badlogic.ashley.core.Component

/***
 * This component is to connect players, bodies etc with
 * controls
 */


class ControlComponent(val keyboard: Boolean = true) : Component