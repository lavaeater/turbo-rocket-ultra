package map.grid

import com.badlogic.gdx.Gdx
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MapLoader {
    fun loadMap(fileName: String): MapData {
        val jsonString = Gdx.files.local(fileName).readString()
        return Json.decodeFromString(jsonString)
    }

    fun saveMap(mapData: MapData) {
        val jsonString = Json.encodeToString(mapData)
        val fileHandle = Gdx.files.local(
            "${
                mapData.name.lowercase().toCharArray().filterNot { it.isWhitespace() }.joinToString("")
            }.json"
        )
        fileHandle.writeString(jsonString, false)
    }

    val keyWords = listOf("name", "start", "success", "fail", "stories", "facts")

    fun loadNewMap(fileName: String): MapData {
        var lines = Gdx.files.local(fileName).readString().lines()

        val mapDefLines = lines.subList(0, lines.indexOfFirst { s -> s.contains("-") })
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
            sections["facts"]!!.joinToString("").trim().toInt(),
            sections["max_spawned_enemies"]!!.joinToString("").trim().toInt(),
            sections["stories"]!!.map { it.trim() },
            mapDefinition
        )

    }
}