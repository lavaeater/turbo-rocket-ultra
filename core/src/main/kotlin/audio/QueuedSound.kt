package audio

import com.badlogic.gdx.audio.Sound

data class QueuedSound(val sound: Sound, val delay: Float, var delta:Float = 0f)