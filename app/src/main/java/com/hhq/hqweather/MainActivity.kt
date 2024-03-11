package com.hhq.hqweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hhq.hqweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val rootBinding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootBinding.root)
        setup()
    }
    private fun setup() {
        rootBinding.goBtn.setOnClickListener {
            Toast.makeText(this,"hello",Toast.LENGTH_SHORT).show()
        }
    }
}