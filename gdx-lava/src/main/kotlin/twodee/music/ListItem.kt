package twodee.music

sealed class ListItem {
    abstract val name: String
    abstract val path: String
    data class SoundFile(override val name: String, override val path: String): ListItem()
    class Directory(
        override val name: String,
        override val path: String,
        val parent: Directory?,
        val childDirs: MutableList<Directory> = mutableListOf(),
        val files: MutableList<SoundFile> = mutableListOf()
    ): ListItem()
    override fun toString(): String {
        return name
    }
}
