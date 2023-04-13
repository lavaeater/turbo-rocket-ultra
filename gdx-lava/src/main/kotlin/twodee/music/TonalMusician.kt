package twodee.music

abstract class TonalMusician(name: String, sampler: Sampler) : Musician(name, sampler) {

    val haveOrWillHavePlayed = mutableMapOf<Int, Boolean>()

    override fun willPlay(noteIndex: Int, intensity: Float): Boolean {
        return if(haveOrWillHavePlayed.contains(noteIndex)) haveOrWillHavePlayed[noteIndex]!! else false
    }

    open fun getNote(notes: List<Note>, minStrength: Float): Note? {
        val filtered = notes.filter { it.strength >= minStrength }
        return if (filtered.any()) filtered.random() else null
    }

    open fun getChordNote(minStrength: Float): Note? {
        return getNote(chord.chordNotes, minStrength)
    }

    open fun getScaleNote(minStrength: Float): Note? {
        return getNote(chord.scaleNotes, minStrength)
    }

    open fun playNote(midiNoteDiff: Int, hitTime: Float) {
        sampler.play(midiNoteDiff, hitTime)
    }
}

