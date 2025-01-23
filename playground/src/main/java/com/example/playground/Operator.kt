package com.example.playground

class Operator {

    data class A(val ticks: Int) {

        operator fun inc() = copy(ticks = ticks + 1)
    }

    fun main() {
        var a = A(0)
        val b = a.inc()
        println(a)
        println(b)
        ++a
        println(a)
    }

}
