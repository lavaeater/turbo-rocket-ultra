package ecs.components.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import ktx.math.random
import ktx.math.vec2

class SeekPlayer: TaskComponent() {
    init {
        coolDownRange = 3f..7f
        coolDown = coolDownRange.random()
    }
    var foundAPlayer = false
    var viewDistance = 30f
    var fieldOfView = 90f
    var scanResolution = 10f
    val maxNumberOfScans get() = fieldOfView / scanResolution
    var scanCount = 0
    var keepScanning = true
    val scanVector = vec2()
    var needsScanVector = true
    val scanVectorStart = vec2()
    val scanVectorEnd = vec2()

    override fun reset() {
        super.reset()
        scanCount = 0
        keepScanning = true
        scanVector.set(Vector2.Zero)
        needsScanVector = true
        scanVectorStart.set(Vector2.Zero)
        scanVectorEnd.set(Vector2.Zero)
        //foundPlayer = null
        foundAPlayer = false
    }
    override fun toString(): String {
        return "seek"
    }

}
