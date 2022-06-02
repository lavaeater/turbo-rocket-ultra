package ecs.systems.input

import com.badlogic.ashley.core.Entity
import ecs.components.intent.IntendsTo
import physics.buildModal
import physics.intendTo
import physics.inBuildMode

class InputActionHandler {
    /**
     * Handles spaceBar, for instance
     */
    fun next(entity: Entity) {
        if (entity.inBuildMode()) {
            entity.buildModal().buildables.nextItem()
        }
    }

    fun previous(entity: Entity) {
        if (entity.inBuildMode()) {
            entity.buildModal().buildables.previousItem()
        }
    }

    /**
     * Depending on mode, selects something - if in buildMode, it will simply BUILD
     */
    fun select(entity: Entity) {
        if (entity.inBuildMode()) {
            entity.intendTo(IntendsTo.Build)
        }
    }

    fun act(entity: Entity) {
        if (entity.inBuildMode()) {
            entity.intendTo(IntendsTo.Build)
        }
    }
}