package ecs.components.fx

import ecs.components.ai.CoolDownComponent

class GibComponent: CoolDownComponent() {
    var hasStopped = false
    override fun reset() {
        super.reset()
        hasStopped = false
    }

    override fun toString(): String {
        return "gibs"
    }
}