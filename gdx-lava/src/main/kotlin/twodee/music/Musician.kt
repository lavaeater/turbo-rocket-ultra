package twodee.music

import com.badlogic.gdx.math.MathUtils

abstract class Musician(override val receiverName: String, protected val sampler: Sampler):
    IMusicSignalReceiver {

    lateinit var chord: Chord
        private set

    override fun setChord(chord: Chord) {
        this.chord = chord
    }

    var lastNoteIndex = 0
    var lastTimeBars = 0f
    protected var beatsPerMeasure = 4f
    protected var beatDuration = 4f
    val notesPerMeasure get() = (beatsPerMeasure * beatDuration).toInt() //16 in our case
    override fun signal(beat: Int, thisNoteIndex: Int, timeBars: Float, hitTime: Float, baseIntensity: Float) {
        lastNoteIndex = MathUtils.floor(lastTimeBars * notesPerMeasure) % notesPerMeasure
        play(beat, thisNoteIndex, timeBars, hitTime, baseIntensity)
        lastTimeBars = timeBars
    }

    override fun updateSignature(beatsPerMeasure: Float, beatDuration: Float) {
        this.beatsPerMeasure = beatsPerMeasure
        this.beatDuration = beatDuration
    }

    abstract fun play(beat: Int, noteIndex: Int, timeBars: Float, hitTime: Float, globalIntensity: Float)

    abstract fun willPlay(noteIndex: Int, intensity: Float): Boolean
}

