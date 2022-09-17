package vn.ztech.software.musicboundservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*

class MusicBoundService : Service() {
    val musicBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    lateinit var messenger: Messenger
    companion object{
        const val ACTION_PLAY = 0
    }
    inner class MusicBinder : Binder() {
        fun getService(): Service{
            return this@MusicBoundService
        }
    }

    inner class MusicServiceHandler: Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                ACTION_PLAY->{

                    startMusic(msg.arg1)
                }
            }
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
        if (!::messenger.isInitialized) messenger = Messenger(MusicServiceHandler())
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}