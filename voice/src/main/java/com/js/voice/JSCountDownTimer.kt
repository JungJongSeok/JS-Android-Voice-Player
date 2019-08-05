package com.js.voice

import android.os.CountDownTimer

class JSCountDownTimer(millisInFuture: Long, countDownInterval: Long, private val listener: JSCountDownTimer.Listener) : CountDownTimer(millisInFuture, countDownInterval) {
    interface Listener {
        fun onFinish()
        fun onTick(millisUntilFinished: Long)
    }

    override fun onFinish() {
        listener.onFinish()
    }

    override fun onTick(millisUntilFinished: Long) {
        listener.onTick(millisUntilFinished)
    }
}