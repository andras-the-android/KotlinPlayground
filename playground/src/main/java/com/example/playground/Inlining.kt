package com.example.playground

fun main() {
    Inlining().run()
}

class Inlining {

    fun run() {
        doSgInlined(action1 = { 5 }) {
            log("actionInlined")
            // non-local return
            // inline function can return without label (= return from run())
            // this is why it's not a compile error that the lambda returns without result
            // doSg call will be skipped entirely
            return
        }
        doSg {
            log("action")
            // not inlined function can only return from it's enclosing function
            return@doSg 5
        }

    }

    inline fun doSgInlined(noinline action1: () -> Int, action2: () -> Int) {
        log("firstInlined")
        val result = action2()
        log("lastInlined $result")
        // this is legal because it's noinline
        emptyLambdaFunction(action1)
        // this is compile error, we can't leak the inlined function arguments
        // doSg(action2)


    }

    fun doSg(action: () -> Int) {
        log("first")
        action()
        log("last")
    }

    fun emptyLambdaFunction(action: () -> Int) {}
}
