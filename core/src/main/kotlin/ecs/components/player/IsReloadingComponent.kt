package ecs.components.player

import ecs.components.ai.old.CoolDownComponent

class IsReloadingComponent: CoolDownComponent() {
    var reloadHasStarted = false
    override fun reset() {
        reloadHasStarted = false
    }
}