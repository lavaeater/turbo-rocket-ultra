package screens

import com.badlogic.gdx.math.Vector3

interface IThing {
    var forwardLimit: ClosedFloatingPointRange<Float>
    var leftLimit: ClosedFloatingPointRange<Float>
    var upLimit: ClosedFloatingPointRange<Float>
    var name: String
    val localPosition: Vector3
    val orientation: Orientation
    val forward: Vector3
    val up: Vector3
    val leftOrRight: Vector3
    val reversePerp: Vector3
    val position: Vector3
    val allThings: Map<String, IThing>
    val children: MutableSet<IThing>
    var parent: IThing?
    var rotateAroundUpEnabled: Boolean
    var rotateAroundLeftEnabled: Boolean
    var rotateAroundForwardEnabled: Boolean
    fun addChild(child: IThing)
    fun removeParent(child: IThing)
    fun rotate(aroundUp: Float, left: Float, aroundForward: Float)
}