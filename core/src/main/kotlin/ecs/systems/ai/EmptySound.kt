package ecs.systems.ai

import com.badlogic.gdx.audio.Sound

class EmptySound : Sound {
    override fun dispose() {

    }

    override fun play(): Long {
        return -1
    }

    override fun play(volume: Float): Long {
        return -1

    }

    override fun play(volume: Float, pitch: Float, pan: Float): Long {
        return -1
    }

    override fun loop(): Long {
        return -1
    }

    override fun loop(volume: Float): Long {
        return -1
    }

    override fun loop(volume: Float, pitch: Float, pan: Float): Long {
        return -1
    }

    override fun stop() {
    }

    override fun stop(soundId: Long) {
    }

    override fun pause() {
    }

    override fun pause(soundId: Long) {
    }

    override fun resume() {
    }

    override fun resume(soundId: Long) {
    }

    override fun setLooping(soundId: Long, looping: Boolean) {
    }

    override fun setPitch(soundId: Long, pitch: Float) {
    }

    override fun setVolume(soundId: Long, volume: Float) {
    }

    override fun setPan(soundId: Long, pan: Float, volume: Float) {
    }
}