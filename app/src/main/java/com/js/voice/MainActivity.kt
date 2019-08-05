package com.js.voice

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import io.storychat.util.JSAudioPlayer

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_PERMISSION_CODE = 123
        private const val RECORD_DURATION_MILLIS = 60 * 1000L
    }

    private var jsAudioRecorder: JSAudioRecorder? = null
    private var jsAudioPlayer: JSAudioPlayer? = null
    private var jsCountDownRecorderTimer: JSCountDownTimer? = null
    private var jsCountDownPlayerTimer: JSCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        content_main_record_button.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
        }
        content_main_record_stop_button.setOnClickListener {
            jsAudioRecorder?.stop()
            jsCountDownRecorderTimer?.cancel()
        }

        content_main_player_button.setOnClickListener {
            if (!File(cacheDir.absolutePath + "/AudioRecording.mp4").exists()) {
                Toast.makeText(this, "Not exists file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            jsAudioPlayer?.stop()
            jsAudioPlayer = JSAudioPlayer(this, MediaPlayer.OnPreparedListener {
                jsCountDownPlayerTimer?.cancel()
                val duration = getAudioDuration(cacheDir.absolutePath + "/AudioRecording.mp4")
                jsCountDownPlayerTimer = JSCountDownTimer(duration, 100, object : JSCountDownTimer.Listener {
                    override fun onFinish() {
                        jsAudioPlayer?.stop()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        content_main_player_time.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                                .format(duration - millisUntilFinished)
                    }
                })
                jsCountDownPlayerTimer?.start()
            }, MediaPlayer.OnCompletionListener {
                jsCountDownPlayerTimer?.cancel()
            })
            jsAudioPlayer?.prepareAndStart()
        }

        content_main_player_stop_button.setOnClickListener {
            jsAudioPlayer?.stop()
            jsCountDownPlayerTimer?.cancel()
        }
    }

    override fun onPause() {
        jsAudioRecorder?.stop()
        jsAudioPlayer?.pause()
        jsCountDownRecorderTimer?.cancel()
        jsCountDownPlayerTimer?.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        jsAudioRecorder?.stop()
        jsAudioRecorder?.release()
        jsAudioPlayer?.stop()
        jsAudioPlayer?.release()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            grantResults.find { it == PackageManager.PERMISSION_DENIED }?.run {
                Toast.makeText(this@MainActivity, "Need Audio record permission", Toast.LENGTH_SHORT).show()
            } ?: run {
                jsAudioRecorder?.stop()
                jsAudioRecorder = JSAudioRecorder(this@MainActivity, RECORD_DURATION_MILLIS.toInt())
                jsAudioRecorder?.prepare()
                jsAudioRecorder?.start()

                jsCountDownRecorderTimer?.cancel()
                jsCountDownRecorderTimer = JSCountDownTimer(RECORD_DURATION_MILLIS, 100, object : JSCountDownTimer.Listener {
                    override fun onFinish() {
                        jsAudioRecorder?.stop()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        content_main_record_time.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                                .format(RECORD_DURATION_MILLIS - millisUntilFinished)
                    }
                })
                jsCountDownRecorderTimer?.start()
            }
        }
    }


    private fun getAudioDuration(path: String): Long {
        val retriever = MediaMetadataRetriever()
        var duration: Long = 0
        try {
            retriever.setDataSource(path)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = java.lang.Long.parseLong(time)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        } finally {
            retriever.release()
        }
        return duration
    }
}
