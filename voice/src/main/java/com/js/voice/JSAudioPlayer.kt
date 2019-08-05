package com.js.voice

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.SeekBar
import java.util.concurrent.atomic.AtomicReference

class JSAudioPlayer(path: String, onPreparedListener: MediaPlayer.OnPreparedListener? = null, onCompletionListener: MediaPlayer.OnCompletionListener) {
    constructor(context: Context, onPreparedListener: MediaPlayer.OnPreparedListener? = null, onCompletionListener: MediaPlayer.OnCompletionListener)
            : this(context.cacheDir.absolutePath + "/AudioRecording.mp4", onPreparedListener, onCompletionListener)

    companion object {
        private const val TAG = "JSAudioPlayer"
    }

    interface OnPauseListener {
        fun onRemainMillis(millis: Int)
    }

    private enum class State {
        INITIAL,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        RELEASED
    }

    private val mediaPlayer = MediaPlayer()
    private val state = AtomicReference(State.INITIAL)

    init {
        mediaPlayer.setDataSource(path)
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            onPreparedListener?.onPrepared(it)
            state.set(State.STARTED)
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionListener.onCompletion(it)
            state.set(State.PREPARED)
        }
    }

    fun prepareAsync() {
        if (state.get() != State.INITIAL
                && state.get() != State.STOPPED) {
            Log.e(TAG, "Please state INITIAL")
            return
        }
        mediaPlayer.prepareAsync()
        state.set(State.PREPARED)
    }

    private fun isPrepared(): Boolean {
        return state.get() == State.PREPARED
    }

    fun isPaused(): Boolean {
        return state.get() == State.PAUSED
    }

    fun isStarted(): Boolean {
        return state.get() == State.STARTED
    }

    fun start() {
        if (state.get() != State.PREPARED
                && state.get() != State.PAUSED
                && state.get() != State.STARTED) {
            Log.e(TAG, "Please state PREPARED or PAUSED or STARTED")
            return
        }
        mediaPlayer.start()
        state.set(State.STARTED)
    }

    fun prepareAndStart() {
        if (isPrepared() || isPaused()) {
            start()
        } else {
            prepareAsync()
        }
    }

    fun seekTo(millis: Int) {
        if (state.get() != State.STARTED
                && state.get() != State.PAUSED
                && state.get() != State.PREPARED) {
            Log.e(TAG, "Please state STARTED or PAUSED or PREPARED")
            return
        }
        mediaPlayer.seekTo(millis)
    }

    fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun pause(onPauseListener: OnPauseListener? = null) {
        if (state.get() != State.STARTED
                && state.get() != State.PAUSED) {
            Log.e(TAG, "Please state STARTED")
            return
        }
        onPauseListener?.onRemainMillis(currentPosition())
        mediaPlayer.pause()
        state.set(State.PAUSED)
    }

    fun pause() {
        pause(null)
    }

    fun stop() {
        if (state.get() != State.STARTED
                && state.get() != State.PAUSED
                && state.get() != State.PREPARED
                && state.get() != State.STOPPED) {
            Log.e(TAG, "Please state STARTED or PAUSED or PREPARED or STOPPED")
            return
        }
        mediaPlayer.stop()
        state.set(State.STOPPED)
    }

    fun release() {
        if (state.get() != State.STOPPED) {
            Log.e(TAG, "Please state STOPPED")
        }
        mediaPlayer.release()
        state.set(State.RELEASED)
    }
}