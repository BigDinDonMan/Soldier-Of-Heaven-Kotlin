package com.soldierofheaven

import com.badlogic.gdx.audio.Sound

//this should have a list of all currently playing sounds and should be able to stop them by id
object SoundManager {
    private val currentlyPlaying = HashSet<Sound>()

    fun pauseAll() = currentlyPlaying.forEach { it.pause() }
    fun resumeAll() = currentlyPlaying.forEach { it.resume() }
    fun stopAll() = currentlyPlaying.forEach { it.stop() }
    fun queue(s: Sound) = currentlyPlaying.add(s)
    fun clearQueue() = currentlyPlaying.clear()
}
