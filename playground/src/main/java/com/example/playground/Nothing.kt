package com.example.playground

import kotlin.Nothing

class Nothing {

    fun aaa(): Nothing {
        throw Exception()
    }

    fun bbb() {
        val a = aaa()
    }
}
