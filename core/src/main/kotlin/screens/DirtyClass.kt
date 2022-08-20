package screens

import kotlin.reflect.KProperty

/**
 * A new idea for all of this, going back to what we had previously, is of course
 * the hierarchical geometry, point clouds etc.
 *
 * But isn't that basically spine? What are we doing here? Will there ever be a new
 * character sprite in this game? Do I want the scout character?
 *
 * Let's make a sort of requirement spec for the character sprite, in Obsidian
 */

open class DirtyClass {
    var dirty = true
    fun setDirty(prop: KProperty<*>, oldValue: Any?, newValue: Any?) {
        if (oldValue != newValue)
            setDirty()
    }

    open fun setDirty() {
        dirty = true
    }
}