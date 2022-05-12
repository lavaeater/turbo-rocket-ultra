package map.grid

import com.badlogic.gdx.Gdx

object MapLoader {

    val keyWords = listOf("name", "start", "success", "fail", "stories", "facts")

    fun loadNewMap(fileName: String): MapData {
        var lines = Gdx.files.local(fileName).readString().lines()

        val mapDefLines = lines.subList(0, lines.indexOfFirst { s -> s.contains('-') })
        val mapDefinition = TextGridMapDefinition(mapDefLines)
        lines = lines.subList(mapDefLines.lastIndex + 2, lines.lastIndex + 1)
        val sections = mutableMapOf<String, MutableList<String>>()
        var inSection = false
        var currentSection = "name"
        for (line in lines) {
            if (inSection) {
                if (!keyWords.contains(line.trim()))
                    sections[currentSection]?.add(line.trim())
                else {
                    currentSection = line.trim()
                    sections[currentSection] = mutableListOf()
                }

            } else {
                if (keyWords.contains(line.trim())) {
                    inSection = true
                    currentSection = line.trim()
                    sections[currentSection] = mutableListOf()
                }
            }
        }

        return MapData(
            sections["name"]!!.joinToString(""),
            sections["start"]!!.joinToString("\n"),
            sections["success"]!!.joinToString("\n"),
            sections["fail"]!!.joinToString("\n"),
            "",
            sections["facts"]!!.toFacts(),
            sections["stories"]!!.map { it.trim() },
            mapDefinition
        )

    }
}

fun List<String>.toFacts() : Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    for(row in this) {
        //Row must be splittable into threes with : as separator, otherwise we skip and log
        val split = row.split(":")
        if(split.size != 3) {
            ktx.log.error { "$row is faulty" }
            break
        }
        map[split[0]] = when(split[2]) {
            "i" -> split[1].toInt()
            "b" -> split[1].toBoolean()
            "f" -> split[1].toFloat()
            else -> split[1]
        }
    }
    return map
}