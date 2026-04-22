package twodee.music

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.math.MathUtils.floor

class SignalConductor(
    private val tempo: Float,
    private val beatsPerMeasure: Float,
    private val beatDuration: Float,
    val instruments: MutableList<IMusicSignalReceiver>,
    val chords: MutableList<Chord>) {
    private val timepiece by lazy { GdxAI.getTimepiece() }
    private val currentTime get() = timepiece.time
    private var startTime = 0f
    private var playing = false
    val notPlaying get() = !playing
    fun play() {
        if(!playing) {
            startTime = currentTime
            for (instrument in instruments) {
                instrument.updateSignature(beatsPerMeasure, beatDuration)
            }
            playing = true
        }
    }

    fun stop() {
        playing = false
    }

    private var lastTimeBars = 0f

    val notesPerMeasure get() = (beatsPerMeasure * beatDuration).toInt()
    private val thisNoteIndex get() = floor(timeBars * notesPerMeasure) % notesPerMeasure

    val thisBar get() = floor(timeSeconds * (tempo / 60f))

    private val lastNoteIndex get() = floor(lastTimeBars * notesPerMeasure) % notesPerMeasure
    private val timeSeconds get() = if (playing) currentTime - startTime else 0f

    private val timeQuarters: Float
        get() {
            return if (playing) {
                (tempo / 60f) * timeSeconds
            } else 0f
        }

    val timeBars: Float
        get() {
            return if (playing) {
                timeQuarters / beatsPerMeasure
            } else 0f
        }

    /** Not sure this is needed now
     *
     */
    private fun barsToEngineTime(timeBars: Float): Float {
        val quarters = timeBars * beatsPerMeasure
        val seconds = quarters / (tempo / 60)
        return startTime + seconds
    }

    private val minIntensity = 0f
    private val maxIntensity = 1f

    var lastBar = 0
    var baseIntensity = 0.2f
    val baseChange = 0.005f
    var change = 0.005f
    var chanceOfChange = 45

    var currentChord = chords.first()

    fun update() {
        /**
         * we send a signal every sixteenth containing the info needed,
         * I guess
         */
        if(playing) {
            //updateIntensity()
            if (lastNoteIndex != thisNoteIndex)
                lastTimeBars = timeBars


            val chordTimeBars = timeBars % chords.size
            var nextChord = chords.firstOrNull { it.barPos > chordTimeBars }
            if (nextChord == null)
                nextChord = chords.first()

            currentChord = nextChord

            val wholeBar = floor(timeBars)
            val barFraction = thisNoteIndex / notesPerMeasure.toFloat()
            val hitTime = barsToEngineTime(wholeBar + barFraction)
            for (receiver in instruments) {
                receiver.setChord(currentChord)
                receiver.signal(thisBar, thisNoteIndex, timeBars, hitTime, baseIntensity)
            }
        }
    }
}
