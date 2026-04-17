package components.player

import components.ai.CoolDownComponent

class IsReloadingComponent: CoolDownComponent() {
    var reloadHasStarted = false
    override fun reset() {
        reloadHasStarted = false
    }
}