package com.example.kotlinplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.playground.CoroutinesExceptions
import com.example.playground.loggerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loggerAdapter = { s -> Log.d("xxx", s) }
        CoroutinesExceptions().exception()
    }
}
