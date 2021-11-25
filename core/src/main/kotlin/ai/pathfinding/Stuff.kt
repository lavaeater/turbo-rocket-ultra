package ai.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Array
import ktx.collections.toGdxArray
import map.grid.Coordinate
import kotlin.math.pow
import kotlin.math.sqrt

class CoordinateHeuristic : Heuristic<Coordinate> {
    override fun estimate(node: Coordinate, endNode: Coordinate): Float {
        return node.hypotenuse(endNode)
    }
}

fun Coordinate.hypotenuse(to: Coordinate): Float {
    val k1 = to.x - this.x
    val k2 = to.y - this.y
    val cost = sqrt(k1.toFloat().pow(2) + k2.toFloat().pow(2))
    return cost
}

class TileConnection(val from: Coordinate, val to: Coordinate) : Connection<Coordinate> {

    override fun getCost(): Float {
        return from.hypotenuse(to)
    }

    override fun getFromNode(): Coordinate {
        return from
    }

    override fun getToNode(): Coordinate {
        return to
    }
}

class TileGraph : IndexedGraph<Coordinate> {
    private val heuristic = CoordinateHeuristic()
    private val coordinates = mutableListOf<Coordinate>()
    private val connections = mutableListOf<TileConnection>()
    private var lastCoordinateIndex = 0
    private val connectionMap = mutableMapOf<Coordinate, MutableList<Connection<Coordinate>>>()

    fun addCoordinate(coordinate: Coordinate) {
        coordinate.index = lastCoordinateIndex
        lastCoordinateIndex++
        coordinates.add(coordinate)
    }

    fun connectCoordinates(from: Coordinate, to:Coordinate) {
        val connection = TileConnection(from, to)
        if(!connectionMap.containsKey(from)) {
            connectionMap[from] = mutableListOf()
        }
        connectionMap[from]!!.add(connection)
        connections.add(connection)
    }

    fun findPath(start: Coordinate, goal: Coordinate) :  GraphPath<Coordinate> {
        val path = DefaultGraphPath<Coordinate>()
        val pathFinder = IndexedAStarPathFinder<Coordinate>(this)
        pathFinder.searchNodePath(start, goal, heuristic, path)
        return path
    }

    override fun getConnections(fromNode: Coordinate): Array<Connection<Coordinate>> {
        return if(connectionMap.containsKey(fromNode)) {
            connectionMap[fromNode]!!.toGdxArray()
        } else {
            emptyArray<Connection<Coordinate>>().toGdxArray()
        }
    }

    override fun getIndex(node: Coordinate): Int {
        return node.index
    }

    override fun getNodeCount(): Int {
        return lastCoordinateIndex
    }

}