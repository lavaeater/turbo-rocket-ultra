package audio

data class QueuedSound(val sound: String, val delay: Float, var delta:Float = 0f)