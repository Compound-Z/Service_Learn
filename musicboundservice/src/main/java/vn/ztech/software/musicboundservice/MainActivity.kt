package vn.ztech.software.musicboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btStart: Button;
    lateinit var btEnd: Button;

    var musicBoundService: MusicBoundService? = null
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBoundService = (service as MusicBoundService.MusicBinder).getService() as MusicBoundService
            musicBoundService?.startMusic(R.raw.tenet)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
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
            unbindService(serviceConnection)
        }
    }
}