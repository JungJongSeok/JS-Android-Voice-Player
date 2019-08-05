# JS-Android-Utils
![ReadMe](https://img.shields.io/github/license/JungJongSeok/JS-Android-Utils)
### You can Illegalagment Exception Safe Voice Recorder, Voice Player

# First
### Get Permission 
AndroidManifest
```
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```
code
```
ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
```
```
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_PERMISSION_CODE) {
        grantResults.find { it == PackageManager.PERMISSION_DENIED }?.run {
            // Denied something
        } ?: run {
            // Success something
        }
    }
}
```

# Function
### Voice Recoder
```
// if you want define path
val jsAudioRecorder = JSAudioRecorder(path, RECORD_DURATION_MILLIS.toInt())
// else ( absolute path = cacheDir.absolutePath + "/AudioRecording.mp4" )
val jsAudioRecorder = JSAudioRecorder(context, RECORD_DURATION_MILLIS.toInt())
```
### Voice Player
```
// if you want define path
val jsAudioPlayer = JSAudioPlayer(path, MediaPlayer.OnPreparedListener {
        // do init something
    }, MediaPlayer.OnCompletionListener {
        // do complete something
    })
// else ( absolute path = cacheDir.absolutePath + "/AudioRecording.mp4" )
val jsAudioPlayer = JSAudioPlayer(this, MediaPlayer.OnPreparedListener {
        // do init something
    }, MediaPlayer.OnCompletionListener {
        // do complete something
    })
```

### Option : CountDownTimer - You can show the text when media player is in progress.
```
val jsCountDownPlayerTimer = JSCountDownTimer(duration, interval, object : JSCountDownTimer.Listener {
    override fun onFinish() {
        // do finish something
    }

    override fun onTick(millisUntilFinished: Long) {
        // do tick something 
    }
})
```

# How to use ?
### Add Project build/gradle
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
### Add defendency
```
dependencies {
    implementation 'com.github.JungJongSeok:JS-Android-Utils:1.0.0'
}
```
