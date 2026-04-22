package twodee.music

//, val scaleNotes: List<Note>
data class Chord(val barPos: Float, val chordNotes: List<Note>, val scaleNotes: List<Note>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chord

        if (barPos != other.barPos) return false
        if (chordNotes != other.chordNotes) return false
        return true
    }

    override fun hashCode(): Int {
        var result = barPos.hashCode()
        result = 31 * result + chordNotes.hashCode()
        return result
    }
}
