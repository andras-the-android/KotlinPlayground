package com.example.kotlinplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playground.MyClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyClass().sayHello()
    }
}