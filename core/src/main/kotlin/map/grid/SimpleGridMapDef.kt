package map.grid

class SimpleGridMapDef(val def: List<String>) {

    fun hasLoot(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'l'
    }

    fun hasStart(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 's'
    }

    fun hasGoal(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'g'
    }

    fun hasObstacle(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'o'
    }

    fun hasBoss(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'b'
    }

    val booleanSections
        get() : Array<Array<Boolean>> {
            return sections.map { column -> column.toCharArray().map { it != 'e' }.toTypedArray() }.toTypedArray()
        }

    val sections
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
        val levelOne = SimpleGridMapDef(
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
        val levelTwo = SimpleGridMapDef(
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