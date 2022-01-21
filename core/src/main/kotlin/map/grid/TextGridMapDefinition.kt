package map.grid

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

fun convert(imagePath: String): TextGridMapDefinition {
    val pixMap = Pixmap(Gdx.files.internal(imagePath))
    val width = pixMap.width
    val height = pixMap.height
    val lines = mutableListOf<String>()
    for (y in 0 until height) {
        lines.add(y, "")
        for (x in 0 until width) {
            val color = pixMap.getPixel(x, y)
            val section = when (color) {
                65535 -> 'b'
                16711935 -> 'g'
                -16776961 -> 'o'
                -65281 -> 'l'
                255 -> 'x'
                -1 -> 's'
                -546232577 -> 'h'
                else -> 'e'
            }
            lines[y] = "${lines[y]}$section"
        }
    }
    pixMap.dispose()
    return TextGridMapDefinition(lines)
}

@Serializable
data class MapFile(val name: String, val startMessage: String, val successMessage: String, val failMessage: String, val mapFile: String) {
    val mapDefinition by lazy { convert(mapFile) }
}

object MapLoader {
    fun loadMap(fileName: String) : MapFile {
        val jsonString = Gdx.files.local(fileName).readString()
        val mapFile = Json.decodeFromString<MapFile>(jsonString)
        return mapFile
    }

    fun saveMap(mapFile: MapFile)  {
        val jsonString = Json.encodeToString(mapFile)
        val fileHandle = Gdx.files.local("${mapFile.name.lowercase().toCharArray().filterNot { it.isWhitespace() }.joinToString("")}.json")
        fileHandle.writeString(jsonString, false)
    }
}

class TextGridMapDefinition(val def: List<String>) : IGridMapDefinition {

    override fun hasLoot(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'l'
    }

    override fun hasStart(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 's'
    }

    override fun hasGoal(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'g'
    }

    override fun hasObstacle(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'o'
    }

    override fun hasBoss(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'b'
    }

    override fun hasHackingStation(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'h'
    }

    override val booleanSections
        get() : Array<Array<Boolean>> {
            return sections.map { column -> column.toCharArray().map { it != 'e' }.toTypedArray() }.toTypedArray()
        }

    private val sections
        get(): Array<Array<Char>> {
            val mapWidth = def.map { it.length }.maxOf { it }
            val mapHeight = def.size
            val s = Array(mapWidth) { x ->
                Array(mapHeight) { y ->
                    def[y][x]
                }
            }
            return s
        }

    companion object {
        val levelZero by lazy { convert("maps/level-0.png") }
        val levelOne by lazy { convert("maps/level-one.png") }
        val levelTwo by lazy { convert("maps/level-two.png") }
        val levelThree by lazy { convert("maps/level-3.png") }
        val levelFour by lazy { convert("maps/level-4.png") }
    }
}

object NewMaps {
    val levelOne by lazy {
        MapFile("Level One", """
            
            You are entering the forbidden sewers, filled with degenerate mutants.
            
            This is your chance to prove yourself. 
            
            Kill 100 enemies to advance.
            
        """.trimIndent(),
        """
            
            You did it! 
            
            100 dead, indeed. Your clothes are stained by their blood and your memories

            haunted by their screams, but there is glory in killing, so you are pleased
            
            You are ready for your next mission
            
        """.trimIndent(),
        """
            
            Awww, that didn't go to well
            
            Not to worry, you will have more opportunities
            
        """.trimIndent(),
            "maps/level-one.png")
    }
    val levelTwo by lazy {
        MapFile("Level Two", """

            Destroy all the Mutato-Emitters, they are turning these hapless and mindless 
            
            creatures into blood-thirsty monsters craving flesh!
            
        """.trimIndent(),
            """
            
            Well done.
            
            We are all very pleased with your performance, keep this up and you will notice
            
            our pleasure on your paycheck.

        """.trimIndent(),
            """
            Awww, that didn't go to well
            Not to worry, you will have more opportunities
        """.trimIndent(),
            "maps/level-two.png")
    }
    fun saveMap(mapFile: MapFile) {
        MapLoader.saveMap(mapFile)
    }
}

