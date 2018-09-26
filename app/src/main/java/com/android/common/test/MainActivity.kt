package com.android.common.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.common.logger.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.info("MainActivity", "MainActivity onCreate")
    }
}
