package com.example.playground

fun main() {
    Infix().start()
}

/**
 * It makes possible function invocation with <receiver function argument> style
 */
class Infix {

    data class SomeClass(val a: Int)

    infix fun SomeClass.concatStr(str: String) = this.a.toString() + str

    fun start() {
        val someClass = SomeClass(10)

        log( someClass concatStr "abc")
    }
}
