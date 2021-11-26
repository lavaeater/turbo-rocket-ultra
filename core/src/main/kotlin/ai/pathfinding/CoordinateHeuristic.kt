package ai.pathfinding

import com.badlogic.gdx.ai.pfa.Heuristic
import ktx.math.vec2
import map.grid.Coordinate

class CoordinateHeuristic : Heuristic<Coordinate> {

    val from = vec2()
    val to = vec2()
    override fun estimate(node: Coordinate, endNode: Coordinate): Float {
        from.set(node.x.toFloat(),node.y.toFloat())
        to.set(endNode.x.toFloat(), endNode.y.toFloat())
        return from.dst(to)
    }
}