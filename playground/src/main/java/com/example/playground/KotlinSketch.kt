package com.example.playground

class KotlinSketch {


    fun start() {
        aaaaa()
    }

    data class jjjj(val a: Int, val b: Int)

    fun aaaaa() {

        infix fun jjjj.concatStr(str: String) = this.a.toString() + str

        val j = jjjj(10, 11)

        log( j concatStr "abc")



    }
}