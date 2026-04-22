package twodee.music

class SignalDrummer(name: String, sampler: Sampler, private val notes: MutableMap<Int, Note>) : Musician(name, sampler) {
    override fun play(beat: Int, noteIndex: Int, timeBars: Float, hitTime: Float, globalIntensity: Float) {
        if (noteIndex == lastNoteIndex)
            return
        val minIntensity = 1f - globalIntensity
        lastNoteIndex = noteIndex
        val note = notes[noteIndex]
        if (note != null && note.strength >= minIntensity)
            sampler.play(note.midiNoteDiff, hitTime)
    }

    override fun willPlay(noteIndex: Int, intensity: Float): Boolean {
        val note = notes[noteIndex]
        return note != null && note.strength >= 1f-intensity
    }
}
