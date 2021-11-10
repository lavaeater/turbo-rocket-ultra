package ecs.components.ai

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class SeekPlayer: TaskComponent() {
    var fieldOfView = 180f
    var scanResolution = 1
    val maxNumberOfScans get() = fieldOfView / scanResolution
    var scanCount = 0
    var keepScanning = true
    val scanVector = vec2()
    var needsScanVector = true
    val scanVectorStart = vec2()
    val scanVectorEnd = vec2()
    override fun reset() {
        scanCount = 0
        keepScanning = true
        scanVector.set(Vector2.Zero)
        needsScanVector = true
        scanVectorStart.set(Vector2.Zero)
        scanVectorEnd.set(Vector2.Zero)
        super.reset()
    }
    override fun toString(): String {
        return "seek"
    }

}
