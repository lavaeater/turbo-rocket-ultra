package ai.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.math.Vector2
import map.grid.Coordinate

class TileConnection(val from: Coordinate, val to: Coordinate) : Connection<Coordinate> {

    override fun getCost(): Float {
        return Vector2.dst(from.x.toFloat(), from.y.toFloat(), to.x.toFloat(), to.y.toFloat())
    }

    override fun getFromNode(): Coordinate {
        return from
    }

    override fun getToNode(): Coordinate {
        return to
    }
}