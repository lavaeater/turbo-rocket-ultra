package audio

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Queue
import ecs.components.AudioChannels
import tru.Assets

class SoundChannel(val name: String, var volume: Float) {
    var nowCoolDown = 0f
    var queueCoolDown = 0f
    private val canPlayNow get() = nowCoolDown <= 0f || name == AudioChannels.simultaneous
    private val soundQueue = Queue<TurboSound>()
    fun playNow(turboSound: TurboSound, volume: Float) {
        if(canPlayNow) {
            this.volume = volume
            nowCoolDown = turboSound.duration
            turboSound.play(volume)
        }
    }

    fun playOnQueue(turboSound: TurboSound) {
        if(soundQueue.isEmpty) {
            queueCoolDown = turboSound.duration
            turboSound.play(volume)
        }
        soundQueue.addLast(turboSound)
    }

    fun update(delta:Float) {
        nowCoolDown -= (delta * 1000f)
        queueCoolDown -= (delta * 1000f)
        updateQueue()
    }

    private fun updateQueue() {
        if(queueCoolDown <= 0f && soundQueue.notEmpty()) {
            soundQueue.removeFirst()
            if(soundQueue.notEmpty()) {
                val s = soundQueue.first()
                queueCoolDown = s.duration
                s.play(volume)
                queueCoolDown = soundQueue.first().duration
            }
        }
    }
}

open class TurboSound(val sound: Sound, val duration: Float) {
    fun play(volume: Float) {
        sound.play(volume)
    }
}

class AudioPlayer(private val defaultVolume : Float = 1f) {
    private val soundChannels = mutableMapOf<String, SoundChannel>()

    fun playNextOnChannel(channel: String, turboSound: TurboSound) {
        if(!soundChannels.containsKey(channel)) {
            soundChannels[channel] = SoundChannel(channel, defaultVolume)
        }
        soundChannels[channel]!!.playOnQueue(turboSound)
    }

    fun getChannel(channel: String) : SoundChannel {
        if(!soundChannels.containsKey(channel)) {
            soundChannels[channel] = SoundChannel(channel, defaultVolume)
        }
        return soundChannels[channel]!!
    }

    fun playOnChannel(channel: String, category: String, sound: String, vol: Float = defaultVolume) {
        getChannel(channel).playNow(getSound(category,sound), vol)
    }

    fun getSound(category: String, sound: String): TurboSound {
        return Assets.newSoundEffects[category]!![sound]!!.random()
    }

    fun playOnChannel(channel: String, turboSound: TurboSound, vol: Float = defaultVolume) {
        getChannel(channel).playNow(turboSound, vol)
    }


    fun update(delta:Float) {
        for((_,q) in soundChannels) {
            q.update(delta)
        }
    }
}

