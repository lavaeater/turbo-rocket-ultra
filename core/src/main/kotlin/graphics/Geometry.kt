package graphics

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

abstract class Geometry(offset: Vector2 = vec2(), rotation: Float = 0f) {
    var worldX: Float by Delegates.observable(offset.x, ::setDirty)
    var worldY: Float by Delegates.observable(offset.y, ::setDirty)
    var localX: Float by Delegates.observable(offset.x, ::setDirty)
    var localY: Float by Delegates.observable(offset.y, ::setDirty)
    val worldPosition: Vector2 = vec2(worldX, worldY)
        get() {
            field.set(worldX, worldY)
            return field
        }
    var worldRotation: Float by Delegates.observable(rotation, ::setDirty)
    var localRotation: Float by Delegates.observable(rotation, ::setDirty)
    val children = mutableListOf<Geometry>()
    var dirty = true

    fun add(child: Geometry): Geometry {
        children.add(child)
        return this
    }

    /**
     * Call this method from any properties that, when changed,
     * will require recalculation of any other properties etc.
     */
    fun setDirty(prop: KProperty<*>, oldValue: Any, newValue: Any) {
        if (oldValue != newValue)
            setDirty()
    }


    private fun setDirty() {
        dirty = true
    }

    /**
     * Called from parent or engine with some kind of baseposition or something
     * I suppose
     */
    protected fun update(parentPosition: Vector2 = Vector2.Zero, parentRotation: Float = 0f) {
        worldX = parentPosition.x + localX
        worldY = parentPosition.y + localY
        worldRotation = parentRotation + localRotation
        updateSelfIfDirty()
        updateChildren()
    }

    private fun updateSelfIfDirty() {
        if (dirty) {
            updateSelf()
            dirty = false
        }
    }

    /**
     * Override with functionality to update the geometry object
     */
    abstract fun updateSelf()

    fun updateChildren() {
        for (child in children) {
            child.update(worldPosition, worldRotation)
        }
    }

    open fun draw(shapeDrawer: ShapeDrawer) {
        for (child in children) {
            child.draw(shapeDrawer)
        }
    }
}