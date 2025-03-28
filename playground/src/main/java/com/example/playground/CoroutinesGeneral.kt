package com.example.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine

/**
 * See also (4 part series) https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21
 * Best practices: https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
 */
class CoroutinesGeneral {

    //    start - main
//    start1 - Thread-0
//    stop - main
//    ended - main
//    start2 - Thread-1
//    stop2 - Thread-1
//    stop1 - Thread-0
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

    //    start - main
//    start1 - pool-1-thread-1
//    stop - main
//    stop1 - pool-1-thread-1
//    start2 - pool-1-thread-1
//    stop2 - pool-1-thread-1
//    ended - main
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
//    rb stop - main  // runBlocking block finishes only after all the local coroutines finished
    fun run1() {
        runBlocking<Unit> {
            prnt("start")
            launch {
                prnt("start1")
                delay(1000L)
                prnt("stop1")
            }
            coroutineScope {
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
        Thread.sleep(2000) // need to keep jvm alive
    }

    //    start - main
//    start rb - main
//    Hello, - main
//    World - DefaultDispatcher-worker-2
//    ! - main
//    stop - main
//    ended - main
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

    //    39  start - main
//    50  stop - main
//    51  task 1 started - main
//    54  task 2 started - main
//    2059  task 2 finished - main
//    2062  task 1 finished - main
//    2062  ended - main
//    for two seconds, everything stops because Thread.sleep - unlike delay -
//    blocks the entire thread, including the other coroutine
    fun threadSleep() {
        runBlocking { // this: CoroutineScope
            prnt("start")
            launch {
                prnt("task 1 started")
                delay(500L)
                prnt("task 1 finished")
            }

            launch { // Creates a coroutine scope
                prnt("task 2 started")
                Thread.sleep(2000L)
                prnt("task 2 finished")
            }

            prnt("stop")
        }
    }

    // coroutine builders can return a result
    fun result() {
        val result = runBlocking {
            return@runBlocking "hello"
        }
        prnt("Result is $result")
    }

    //    25  start - main
//    39  start1 - main
//    243  call start job two - main
//    243  start2 - main
//    1044  stop1 - main
//    1249  stop2 - main
//    1252  The answer is 42 - main
//    1253  ended - main
    fun async() = runBlocking {
        prnt("start")
        // this one will start instantly
        val one = async {
            prnt("start1")
            delay(1000)
            prnt("stop1")
            return@async 40
        }
        // this one will run after Deferred.start call
        val two = async(start = CoroutineStart.LAZY) {
            prnt("start2")
            delay(1000)
            prnt("stop2")
            return@async 2
        }
        delay(200)
        prnt("call start job two")
        two.start() // start the second one
        prnt("The answer is ${one.await() + two.await()}")
    }

//    20  Main - main
//    42  calling coroutineScope - main @coroutine#1
//    1045  I'm in a coroutine scope - main @coroutine#1
//    1046  calling launch - main @coroutine#1
//    1102  Second new coroutine - main @coroutine#3
//    1555  First new coroutine - main @coroutine#2
//    1556  ended - main

    //    coroutineScope suspends the execution of the caller and starts it's work on the same coroutine
    fun launchVsCoroutineScope() {
        prnt("Main")
        runBlocking {
            prnt("calling coroutineScope")
            // "This function returns as soon as the given block and all its children coroutines are completed."
            coroutineScope {
                delay(1000)
                prnt("I'm in a coroutine scope")
            }
            prnt("calling launch")
            launch {
                delay(500)
                prnt("First new coroutine")
            }
            launch {
                delay(50)
                prnt("Second new coroutine")
            }
        }
    }

    private fun callbackBasedMethod(callback: (String) -> Unit) {
        callback("important result")
    }

    suspend fun convertCallbacksToCoroutine(): String = suspendCoroutine { continuation ->
        callbackBasedMethod { result ->
            continuation.resumeWith(Result.success(result))
        }
    }
}

fun main() {
    CoroutinesGeneral().run1()
    prnt("ended")
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")
