package com.js.voice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.util.Log
import java.util.concurrent.atomic.AtomicReference

class JSAudioRecorder(val path: String, duration: Int) {
    constructor(context: Context, duration: Int) : this(context.cacheDir.absolutePath + "/AudioRecording.mp4", duration)

    private enum class State {
        INITIAL,
        PREPARED,
        RECORDING,
        RELEASED
    }

    companion object {
        private const val TAG = "JSAudioRecorder"
        private const val BIT_DEPTH = 4
        private const val SAMPLE_RATE = 44100
        private const val BIT_RATE = SAMPLE_RATE * BIT_DEPTH
    }

    private val mediaRecord = MediaRecorder()
    private val state = AtomicReference(State.INITIAL)

    init {
        mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecord.setOutputFile(path)

        mediaRecord.setAudioEncodingBitRate(BIT_RATE)
        mediaRecord.setAudioSamplingRate(SAMPLE_RATE)
        mediaRecord.setMaxDuration(duration)
        mediaRecord.setOnErrorListener { _, _, _ ->
            state.set(State.INITIAL)
        }
    }

    fun prepare() {
        if (state.get() != State.INITIAL) {
            Log.e(TAG, "Please state INITIAL")
            return
        }
        mediaRecord.prepare()
        state.set(State.PREPARED)
    }

    fun reset() {
        if (state.get() == State.RELEASED) {
            Log.e(TAG, "Please do not state RELEASED")
            return
        }
        mediaRecord.reset()
        state.set(State.INITIAL)
    }

    fun start() {
        if (state.get() != State.PREPARED) {
            Log.e(TAG, "Please state PREPARED")
            return
        }
        mediaRecord.start()
        state.set(State.RECORDING)
    }

    fun stop() {
        if (state.get() != State.RECORDING) {
            Log.e(TAG, "Please state RECORDING")
            return
        }
        try {
            mediaRecord.stop()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        } finally {
            state.set(State.INITIAL)
        }
    }

    fun release() {
        if (state.get() != State.INITIAL) {
            Log.e(TAG, "Please state INITIAL")
            return
        }
        mediaRecord.release()
        state.set(State.RELEASED)
    }

    @Throws(NumberFormatException::class)
    fun getRecordTimeMills(): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return Integer.parseInt(time)
    }
}