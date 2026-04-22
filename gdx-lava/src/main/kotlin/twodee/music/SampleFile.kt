package twodee.music

data class SampleFile(var name: String, var path: String, val excludes: List<String>) {
    private fun extractTags(): List<String> {
        var splitPath = path.split("/").toMutableList()
        splitPath = splitPath.map { it.replace("musicradar-", "") }.toMutableList()
        splitPath.remove(splitPath.first { it.contains(".wav") })
        splitPath.removeAll(excludes)
        return splitPath
    }

    val tags = extractTags()


    override fun toString(): String {
        return if (tags.size > 1)
            if (tags.size > 2)
                if (tags.first() != tags[1])
                    "${tags.first()} - ${tags[1]} - $name"
                else "${tags.first()} - ${tags[2]} - $name"
            else
                "${tags.first()} - ${tags[1]} - $name"
        else
            "${tags.first()} - $name"
    }
}
