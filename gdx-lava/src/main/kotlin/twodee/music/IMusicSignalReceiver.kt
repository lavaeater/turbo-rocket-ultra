package twodee.music

interface IMusicSignalReceiver {
    val receiverName: String
    fun signal(beat: Int, thisNoteIndex: Int, timeBars: Float, hitTime: Float, baseIntensity: Float)
    fun setChord(chord: Chord)
    fun updateSignature(beatsPerMeasure: Float, beatDuration: Float)
}
