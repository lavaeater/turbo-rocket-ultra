package map.grid

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap

fun convert(imagePath: String): TextGridMapDefinition {
    val pixMap = Pixmap(Gdx.files.internal(imagePath))
    val width = pixMap.width
    val height = pixMap.height
    val lines = mutableListOf<String>()
    for(y in 0 until height) {
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
                else -> 'e'
            }
            lines[y] = "${lines[y]}$section"
        }
    }
    return TextGridMapDefinition(lines)
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



        val levelOne = convert("maps/level-one.png")
        val levelTwo = TextGridMapDefinition(
            """
            xxxxgoxxb
            xeeeexeee
            xeeeexxxl
            xxlxxxeex
            xxxeeloxx
            xxeeeeeex
            sxxxxxxxo
        """.trimIndent().lines()
        )
        /*


         */
        val levelThree = TextGridMapDefinition(
            """
                xxxxxxxxxxxxxxxxxxxxxx
                xeeeeeeeeeeeeeeeeeeeex
                xexxxxxxxxoxxxxxxxxxxx
                xexeeeeeeeeeeeeeeeeeee
                xexexxxxxxxxxxxxxxxxxx
                xexexeeeeeeeeeeeeeeeex
                xexexexxxxxxxxxxxxxxxx
                xexexexeeeeeeeeeeeeeee
                xexexexxexxxxxxxxxxxxe
                xexxxegbexboxeeeeeeexe
                xeeeeeeeexooxexxxxoexe
                xxxxxxxxxxooxeoeexxexe
                eoeleoelexxxxxxeebgexe
                eeeeeeeeeeeeeeeeeeeexe
                eexxxxxxxxxxxxxxxxxxxe
                exxeeeeeeeeeeeeeeeeeee
                exeeeeeeeeeeexooeeeeee
                exxxxxxxxxxxxlooeeeeee
                exeeeeeeeeeeexxxeeeeee
                sxeeeeeeeeeeeeeeeeeeee
            """.trimIndent().lines()
        )
    }
}