package vn.ztech.software.foregroundservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ForegroundService : Service() {
    companion object{
        val ACTION_CANCEL = 0
        val ACTION_PLAY = 1
        val ACTION_PAUSE = 2
        val ACTION_START = 3
    }

    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    lateinit var currSong: Song
    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        bundle?.let {
            val song = it.getParcelable<Song>("SONG")
            if (song != null){
                currSong = song
                playMusic(song)
                startForeground(1, getNoti(song?:Song("","",0,0)))
            }
        }

        val action = intent?.getIntExtra("ACTION_MUSIC", -1)
        handleAction(action, 1)
        return START_STICKY
    }

    private fun handleAction(action: Int?, startId: Int) {
        when(action){
            ACTION_CANCEL->{
                stopSelf()
            }
            ACTION_PLAY->{
                resumeMusic(startId)
            }
            ACTION_PAUSE->{
                pauseMusic(startId)
            }
            else->{
            }
        }
    }

    private fun resumeMusic(startId: Int) {
        if (mediaPlayer!=null && !isPlaying){
            mediaPlayer?.let {
                it.start()
                isPlaying = true
                startForeground(startId, getNoti(currSong?:Song("","",0,0)))
                sendIntentToActivity(ACTION_PLAY)
            }
        }
    }

    private fun pauseMusic(startId: Int) {
        if (mediaPlayer!=null && isPlaying){
            mediaPlayer?.let {
                it.pause()
                isPlaying = false
                startForeground(startId, getNoti(currSong?:Song("","",0,0)))
                sendIntentToActivity(ACTION_PAUSE)
            }
        }
    }

    private fun playMusic(song: Song) {
        if (mediaPlayer==null){
            mediaPlayer = MediaPlayer.create(applicationContext, song.uri)
        }
        mediaPlayer?.start()
        isPlaying = true
        sendIntentToActivity(ACTION_START)
    }

    override fun onDestroy() {
        Log.d("xxx","destroy")
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        mediaPlayer?.let {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        sendIntentToActivity(ACTION_CANCEL)
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun getNoti(data: Song): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val remoteView = RemoteViews(packageName, R.layout.layout_noti)
        remoteView.setTextViewText(R.id.tvName, data.name)
        remoteView.setTextViewText(R.id.tvSinger, data.single)

        val imgBitmap = BitmapFactory.decodeResource(resources, R.drawable.tenet)
        remoteView.setImageViewBitmap(R.id.ivSong, imgBitmap)

        if (isPlaying)remoteView.setImageViewResource(R.id.btPlayOrPause, R.drawable.ic_baseline_pause_circle_filled_24)
        else remoteView.setImageViewResource(R.id.btPlayOrPause, R.drawable.ic_baseline_play_circle_filled_24)

        remoteView.setOnClickPendingIntent(R.id.btCancel, getPendingIntent(ACTION_CANCEL))
        remoteView.setOnClickPendingIntent(R.id.btPlayOrPause, if (isPlaying) getPendingIntent(ACTION_PAUSE) else getPendingIntent(ACTION_PLAY))
        return NotificationCompat.Builder(applicationContext,"foreground_service_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setChannelId("foreground_service_channel")
            .setCustomContentView(remoteView)
            .setContentIntent(pendingIntent)
            .setSound(null)
            .build()
    }

    private fun getPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, Receiver::class.java)
        intent.putExtra("ACTION_MUSIC", action)
        return PendingIntent.getBroadcast(this, action, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun sendIntentToActivity(action: Int){
        val intent = Intent("ACTION_UPDATE_MUSIC")
        val bundle = Bundle()
        bundle.putParcelable("SONG", currSong)
        bundle.putInt("ACTION", action)
        bundle.putBoolean("IS_PLAYING", isPlaying)
        intent.putExtras(bundle)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


}