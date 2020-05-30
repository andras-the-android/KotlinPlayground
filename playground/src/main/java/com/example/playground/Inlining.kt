package com.example.playground

fun main() {
    Inlining().run()
}

class Inlining {

    fun run() {
        doSgInlined { log("actionInlined") }
        doSg { log("action") }

    }

    inline fun doSgInlined(action: () -> Unit) {
        log("firstInlined")
        action()
        log("lastInlined")
//        this is compile error, we can't leak the inlined function arguments
//        doSg(action)

    }

    fun doSg(action: () -> Unit) {
        log("first")
        action()
        log("last")
    }
}