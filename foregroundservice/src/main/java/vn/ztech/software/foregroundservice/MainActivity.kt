package vn.ztech.software.foregroundservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import vn.ztech.software.foregroundservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var isPlaying = false

    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            bundle?.let {

                val song = it.getParcelable<Song>("SONG")
                isPlaying = it.getBoolean("IS_PLAYING")
                val action = it.getInt("ACTION")
                song?.let {
                    handleAction(action, song)
                }
            }
        }

    }

    private fun handleAction(action: Int, song: Song) {

        when(action){
            ForegroundService.ACTION_START->{
                binding.layoutMusic.root.visibility = View.VISIBLE
                binding.layoutMusic.ivSong.setImageResource(R.drawable.tenet)
                binding.layoutMusic.tvName.text = song.name
                binding.layoutMusic.tvSinger.text = song.single
                binding.layoutMusic.btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

                binding.layoutMusic.btPlayOrPause.setOnClickListener {
                    sendIntentToService(if (isPlaying) ForegroundService.ACTION_PAUSE else ForegroundService.ACTION_PLAY)
                }
                binding.layoutMusic.btCancel.setOnClickListener {
                    sendIntentToService(ForegroundService.ACTION_CANCEL)
                }
            }
            ForegroundService.ACTION_PLAY->{
                if (isPlaying){
                    binding.layoutMusic.btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                }
            }
            ForegroundService.ACTION_PAUSE->{
                if (!isPlaying){
                    binding.layoutMusic.btPlayOrPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            }
            ForegroundService.ACTION_CANCEL->{
                binding.layoutMusic.root.visibility = View.GONE
            }
        }

    }

    private fun sendIntentToService(action: Int) {
        val intent = Intent(this, ForegroundService::class.java)
        intent.putExtra("ACTION_MUSIC", action)
        startService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("ACTION_UPDATE_MUSIC"))

        createChannel()
        binding.btService.setOnClickListener {

            val intent = Intent(this, ForegroundService::class.java)
            val bundle = Bundle()
            bundle.putParcelable("SONG",Song("Rainy night in Tallin", "Tenet",R.raw.tenet, R.drawable.tenet))
            intent.putExtras(bundle)
            startService(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        val nm:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val nc = NotificationChannel("foreground_service_channel", "Foreground service channel", NotificationManager.IMPORTANCE_DEFAULT)
        nc.description = "This is notification for foreground service test app"
        nc.enableLights(true)
        nc.setShowBadge(true)
        nc.setSound(null, null)
        nm.createNotificationChannel(nc)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}