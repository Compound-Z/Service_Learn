package vn.ztech.software.servicetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import vn.ztech.software.servicetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.start.setOnClickListener {
            Toast.makeText(this,"Xxx",Toast.LENGTH_SHORT).show()
            Log.d("xxxx","xxxx")
            startService(Intent(this, MyService::class.java))
        }
        binding.stop.setOnClickListener {
            stopService(Intent(this, MyService::class.java))
        }
    }
}