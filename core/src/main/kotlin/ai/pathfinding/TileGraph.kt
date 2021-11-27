package ai.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Array
import ktx.collections.toGdxArray
import map.grid.Coordinate

class TileGraph : IndexedGraph<Coordinate> {
    private val heuristic = CoordinateHeuristic()
    private val connections = mutableListOf<TileConnection>()
    private val connectionMap = mutableMapOf<Coordinate, MutableList<Connection<Coordinate>>>()

    companion object {
        private val coordinates = mutableSetOf<Coordinate>()
        private var lastCoordinateIndex = 0
        fun getCoordinateInstance(x: Int, y: Int): Coordinate {
            val coord = Coordinate(x, y)
            val added = coordinates.add(coord)
            if (added) {
                coord.index = lastCoordinateIndex
                lastCoordinateIndex++
            }
            return if (added) coord else coordinates.first { it == coord }
        }
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
        val pathFinder = IndexedAStarPathFinder(this)
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
        return coordinates.first { it == node }.index
    }

    override fun getNodeCount(): Int {
        return lastCoordinateIndex
    }

}