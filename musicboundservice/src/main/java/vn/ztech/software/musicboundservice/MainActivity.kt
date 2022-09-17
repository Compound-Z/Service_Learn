package vn.ztech.software.musicboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btStart: Button;
    lateinit var btEnd: Button;

    var musicBoundService: MusicBoundService? = null
    lateinit var messenger: Messenger
    var isPlaying = false
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            musicBoundService = (service as MusicBoundService.MusicBinder).getService() as MusicBoundService
//            musicBoundService?.startMusic(R.raw.tenet)
            messenger = Messenger(service)
            isPlaying = true
            //send msg
            val msg = Message.obtain(null, MusicBoundService.ACTION_PLAY,R.raw.tenet,0)
            messenger.send(msg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPlaying = false
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btStart = findViewById(R.id.btStart)
        btEnd = findViewById(R.id.btEnd)

        btStart.setOnClickListener {
            val intent = Intent(this, MusicBoundService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        btEnd.setOnClickListener {
            if (isPlaying){
                unbindService(serviceConnection)
                isPlaying = false
            }
        }
    }
}