package vn.ztech.software.musicboundservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicBoundService : Service() {
    val musicBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    inner class MusicBinder : Binder() {
        fun getService(): Service{
            return this@MusicBoundService
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun startMusic(raw: Int){
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(applicationContext, raw)
        }
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null){
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}