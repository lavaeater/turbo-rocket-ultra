package ecs.components.player

import ecs.components.ai.CoolDownComponent

class IsReloadingComponent: CoolDownComponent() {
    var reloadHasStarted = false
    override fun reset() {
        reloadHasStarted = false
    }
}