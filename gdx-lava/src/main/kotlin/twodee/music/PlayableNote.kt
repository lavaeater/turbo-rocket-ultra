package twodee.music

import com.badlogic.gdx.audio.Sound

data class PlayableNote(val soundSource: Sound, val pitch: Float, val targetTime: Float)
