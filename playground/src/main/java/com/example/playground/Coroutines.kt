package com.example.playground

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Coroutines {

    fun runThread() {
        prnt("start")
        Thread {
            prnt("start1")
            Thread.sleep(1000L)
            prnt("stop1")
        }.start()
        Thread {
            prnt("start2")
            Thread.sleep(500L)
            prnt("stop2")
        }.start()
        prnt("stop")

    }

    fun runExecutors() {
        prnt("start")
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            prnt("start1")
            Thread.sleep(1000L)
            prnt("stop1")
        }
        executor.submit {
            prnt("start2")
            Thread.sleep(500L)
            prnt("stop2")
        }
        prnt("stop")
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS)

    }


//    start - main
//    stop - main
//    start1 - main
//    start2 - main
//    stop2 - main
//    stop1 - main
//    rb stop - main  //runBlocking block finishes only after all the local coroutines finished
    fun run1() {
        runBlocking<Unit> {
            prnt("start")
            launch {
                prnt("start1")
                delay(1000L)
                prnt("stop1")
            }
            launch {
                prnt("start2")
                delay(500L)
                prnt("stop2")
            }
            prnt("stop")
        }
        prnt("rb stop")
    }

//    start - main
//    start1 - DefaultDispatcher-worker-1 interesting that this starts on one thread and finishes on another
//    stop - main
//    start2 - DefaultDispatcher-worker-2
//    rb stop - main   runBlocking block doesn't wait for the tasks because they are running in a different scope
//    stop2 - DefaultDispatcher-worker-2
//    stop1 - DefaultDispatcher-worker-2
    fun run2() {
        runBlocking<Unit> {
            prnt("start")
            GlobalScope.launch {
                prnt("start1")
                delay(1000L)
                prnt("stop1")
            }
            GlobalScope.launch {
                prnt("start2")
                delay(500L)
                prnt("stop2")
            }
            prnt("stop")
        }
        prnt("rb stop")
        Thread.sleep(2000) //need to keep jvm alive
    }

    fun join() {
        prnt("start")
        runBlocking<Unit> { // start main coroutine
            prnt("start rb")
            val job = GlobalScope.launch { // launch a new coroutine in background and continue
                delay(2000L)
                prnt("World")
            }
            delay(1000L)
            prnt("Hello,")
            job.join()
            prnt("!")
        }
        prnt("stop")
    }

    /**
     * According to the documentation runBlocking blocks the thread and coroutineScope doesn't.
     * But despite this task1 finishes first I don't undersatnd why
     */
    fun scope() {
        runBlocking { // this: CoroutineScope
            prnt("start")
            launch {
                prnt("task 1 started")
                delay(500L)
                prnt("task 1 finished")
            }

            runBlocking { // Creates a coroutine scope
                prnt("task 2 started")
                delay(2000L)
                prnt("task 2 finished")
            }

            prnt("stop")
        }
    }
}

fun main() {
    Coroutines().scope()
}

fun prnt(s: String) = println(s + " - " + Thread.currentThread().name)