package twodee.music

import com.badlogic.gdx.math.MathUtils
import ktx.math.random

class SignalBass(name: String, sampler: Sampler) : TonalMusician(name, sampler) {
    override fun play(beat: Int, noteIndex: Int, timeBars: Float, hitTime: Float, globalIntensity: Float) {
        if (lastNoteIndex == noteIndex)
            return

        val wholeBar = MathUtils.floor(timeBars)
        haveOrWillHavePlayed[noteIndex] = false
        val barFraction = noteIndex / notesPerMeasure.toFloat()
        val noteTime = wholeBar + barFraction
        if (noteIndex == 0) {
            val n = getChordNote(1f)
            if (n != null) {
                playNote(n.midiNoteDiff, noteTime)
                haveOrWillHavePlayed[noteIndex] = true
                return
            }
        }

        if (noteIndex % beatsPerMeasure.toInt() == 0) {
            if ((0f..1f).random() <= globalIntensity) {
                val n = getChordNote(0.5f)
                if (n != null) {
                    haveOrWillHavePlayed[noteIndex] = true
                    playNote(n.midiNoteDiff, noteTime)
                    return
                }
            }
        }

        if (noteIndex % (beatsPerMeasure / 2).toInt() == 0) {
            if ((0f..1f).random() <= globalIntensity - 0.25f) {
                val n = getChordNote(0.25f)
                if (n != null) {
                    haveOrWillHavePlayed[noteIndex] = true
                    playNote(n.midiNoteDiff, noteTime)
                    return
                }
            }
        }
        if ((0f..1f).random() <= globalIntensity - 0.5f) {
            val n = getChordNote(0f)
            if (n != null) {
                haveOrWillHavePlayed[noteIndex] = true
                playNote(n.midiNoteDiff, noteTime)
            }
        }
    }
}
