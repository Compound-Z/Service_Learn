package vn.ztech.software.foregroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getIntExtra("ACTION_MUSIC",-1)

        val intent = Intent(context, ForegroundService::class.java)
        intent.putExtra("ACTION_MUSIC", action)

        context?.startService(intent)
    }
}