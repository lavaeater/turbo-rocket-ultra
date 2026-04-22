package twodee.music

import com.badlogic.gdx.math.MathUtils.*
import twodee.core.selectedItemListOf
import ktx.math.random

class SoloMusician(name: String, inputSamplers: List<Sampler>, private val recordBars: Int = 4, private val repeats: Int = 1) :
    TonalMusician(name, inputSamplers.first()) {

    private var recordedMelody = Array(recordBars * notesPerMeasure) { Note(0, 1f, false) }
    private var repeatBar = -999f
    private val randomRange = 0f..1f

    private val samplers = selectedItemListOf(*inputSamplers.toTypedArray())

    override fun playNote(midiNoteDiff: Int, hitTime: Float) {
        samplers.selectedItem.play(midiNoteDiff, hitTime)
    }

    override fun updateSignature(beatsPerMeasure: Float, beatDuration: Float) {
        super.updateSignature(beatsPerMeasure, beatDuration)
        recordedMelody = Array(recordBars * notesPerMeasure) { Note(0, 1f, false) }
    }

    override fun play(beat: Int, noteIndex: Int, timeBars: Float, hitTime: Float, globalIntensity: Float) {
        if(noteIndex == lastNoteIndex)
            return

        val wholeBar = floor(timeBars)
        haveOrWillHavePlayed[noteIndex] = false
        val recordingIdx = noteIndex + wholeBar % recordBars * notesPerMeasure

        val repeatEndBars = repeatBar + repeats * recordBars

        if (timeBars < repeatEndBars) {
            val note = recordedMelody[recordingIdx]
            if (note.realNote) {
                haveOrWillHavePlayed[noteIndex] = true
                playNote(note.midiNoteDiff, hitTime)
            }
        } else {
            if (recordingIdx == 0) {
                samplers.nextItem()
                recordedMelody = Array(recordBars * notesPerMeasure) { Note(0, 1f, false) }
            }
            var note: Note? = null

            // always play a strong note on the downbeat
            if (noteIndex == 0) {
                note = getScaleNote(1.0f)
            } else if (noteIndex % beatsPerMeasure.toInt() == 0) {
                if (randomRange.random() < globalIntensity) {
                    note = getScaleNote(0.5f)
                }
            } else if (noteIndex % (beatsPerMeasure / 2).toInt() == 0) {
                if (randomRange.random() < globalIntensity - 0.25f) {
                    note = getScaleNote(0.25f)
                }
            } else if (randomRange.random() < globalIntensity - 0.5f) {
                note = getScaleNote(0.0f)
            }

            // record and play the note
            recordedMelody[recordingIdx] = note ?: recordedMelody[recordingIdx]

            if (note != null) {
                haveOrWillHavePlayed[noteIndex] = true
                playNote(note.midiNoteDiff, hitTime)
            }

            // if we're done recording, start repeating
            val lastRecordingIdx = recordBars * notesPerMeasure - 1
            if (recordingIdx >= lastRecordingIdx) {
                repeatBar = kotlin.math.ceil(timeBars)
            }
        }
    }
}
