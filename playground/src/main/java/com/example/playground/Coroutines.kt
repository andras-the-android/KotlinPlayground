package com.example.playground

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine

/**
 * See also (4 part series) https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21
 * Best practices: https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
 */
class Coroutines {

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

//    48  job1: I run in GlobalScope and execute independently! - DefaultDispatcher-worker-1
//    154  job2: I am a child of the request coroutine - main
//    548  call cancel - main
//    1062  job1: I am not affected by cancellation of the request - DefaultDispatcher-worker-1
//    1557  ended - main
    fun cancellation() = runBlocking<Unit> {
        // launch a coroutine to process some kind of incoming request
        val job = launch {
            // it spawns two other jobs, one with GlobalScope
            GlobalScope.launch {
                prnt("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                prnt("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                prnt("job2: I am a child of the request coroutine")
                delay(1000)
                prnt("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        prnt("call cancel")
        job.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
    }

    // nulling out the scope won't cancel it
    fun cancelByScope() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            repeat(10) {
                prnt("round #$it")
                delay(500)
            }
        }
        delay(1100)
        prnt("cancelling...")
        scope.cancel()
        delay(5000) // keep jvm alive
    }

    // cancellable functions throw a JobCancellationException when the scope is cancelled
    // catching this exception can prevent the cancellation of the job
    fun cancellationException() = runBlocking {
        val job = launch(Dispatchers.Default) {
            repeat(10) { i ->
                try {
                    // print a message twice a second
                    prnt("job: I'm sleeping $i ...")
                    cancellableDelayFunction()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // removing this line would prevent the cancellation
                    throw e
                }
            }
        }
        delay(1300L) // delay a bit
        prnt("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        prnt("main: Now I can quit.")
    }

    private suspend fun cancellableDelayFunction() {
        // throws JobCancellationException if the scope is cancelled
        yield()
        // delay is cancellable by itself so it's not good for this demonstration
        // unless we call it from this special context
        withContext(NonCancellable) {
            delay(500)
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

    private val scope = CoroutineScope(Dispatchers.Unconfined + CoroutineName("outer scope"))

    private val handler1 = CoroutineExceptionHandler { _, exception ->
        prnt("CoroutineExceptionHandler1 got $exception")
    }

    private val handler2 = CoroutineExceptionHandler { _, exception ->
        prnt("CoroutineExceptionHandler2 got $exception")
    }

    private val handler3 = CoroutineExceptionHandler { _, exception ->
        prnt("CoroutineExceptionHandler3 got $exception")
    }

//    30  starting - main @coroutine#1
//    106  throwing ex1 - kotlinx.coroutines.DefaultExecutor @gyaaa#2
//    127  cancelled - kotlinx.coroutines.DefaultExecutor @coroutine#3
//    128  CoroutineExceptionHandler got java.lang.Exception: ex1 - kotlinx.coroutines.DefaultExecutor @coroutine#3
//    356  ended - main

//    it's really interesting that the exception handler uses coroutine #3
    fun exception() {
        scope.launch(handler1) {
            try {
                prnt("starting")
                // putting an exception handler here would do nothing because it has a parent
                launch() {
                    delay(50)
                    prnt("throwing ex1")
                    throw Exception("ex1")
                }
                launch {
                    try {
                        delay(100000)
                        prnt("finishing")
                    } finally {
                        // this one is cancelled because of the the exception on the other coroutine
                        prnt("cancelled")
                    }
                }
            } catch (e: Exception) {
                // this wont be called
            }

        }

        // keep the jvm alive.
        // Without that, the CoroutineExceptionHandler on the root element would never be invoked
        Thread.sleep(300)
    }

    fun exception2() {
        try {
            // runBlocking passes through the exception
            runBlocking {
                launch {
                    delay(50)
                    prnt("throwing ex1")
                    throw Exception("ex1")
                }
            }
        } catch (e: Exception) {
            prnt("caught")
        }
    }

    fun exception3() {
        scope.launch(handler1) {
            val deferred = async(start = CoroutineStart.LAZY) {
                throw Exception("ex1")
            }
            try {
                deferred.await()
            } catch (e: Exception) {
                // unlike launch, this exception is catchable but it cancels the scope anyway
                prnt("caught")
            }
        }
    }

    fun exception4() {
        scope.launch(handler1) {
            val deferred = async(start = CoroutineStart.LAZY) {
                delay(1000)
            }
            deferred.cancel()
            try {
                // waiting for a cancelled coroutine throws exception well
                deferred.await()
            } catch (e: Exception) {
                prnt("caught")
            }
        }
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

//    36  starting supervisor - main @coroutine#1
//    this job: "coroutine#1":SupervisorCoroutine{Active}@7d0587f1, parent job: "coroutine#1":StandaloneCoroutine{Active}@5d76b067
//    this job: "sub 1#2":StandaloneCoroutine{Active}@7a765367, parent job: "coroutine#1":SupervisorCoroutine{Completing}@7d0587f1
//    this job: "sub 1#3":StandaloneCoroutine{Active}@60285225, parent job: "coroutine#1":SupervisorCoroutine{Completing}@7d0587f1
//    116  throwing ex1 - kotlinx.coroutines.DefaultExecutor @sub 1#2
//    117  CoroutineExceptionHandler1 got java.lang.Exception: ex1 - kotlinx.coroutines.DefaultExecutor @sub 1#2
//    1069  finishing - kotlinx.coroutines.DefaultExecutor @sub 1#3
//    1069  supervisor scope finished - kotlinx.coroutines.DefaultExecutor @coroutine#1
//    2069  ended - main
//
//    this is basically the same as exception() but because of the supervisorJob, the second
//    coroutine won't be canceled
    fun supervisor1() {
        scope.launch(handler1) {
            supervisorScope {
                prnt("starting supervisor")
                printJobInfo(this)
                launch(CoroutineName("sub 1")) {
                    printJobInfo(this)
                    delay(50)
                    prnt("throwing ex1")
                    throw Exception("ex1")
                }
                launch(CoroutineName("sub 1")) {
                    try {
                        printJobInfo(this)
                        delay(1000)
                        // it's not canceled
                        prnt("finishing")
                    } catch (e: Exception) {
                        print("exception $e")
                        throw e
                    }

                }
            }
            // supervisorScope() builder suspends the current coroutine until all child job is finished
            prnt("supervisor scope finished")
        }
        // keep the jvm alive.
        // Without that, the CoroutineExceptionHandler on the root element would never be invoked
        Thread.sleep(2000)
    }

    fun supervisor2() {
        scope.launch(handler1) {
            val supervisorScope = CoroutineScope(coroutineContext + SupervisorJob())
            prnt("starting supervisor")
            supervisorScope.launch {
                delay(50)
                prnt("throwing ex1")
                throw Exception("ex1")
            }
            supervisorScope.launch {
                try {
                    delay(1000)
                    // it's not canceled
                    prnt("finishing")
                } catch (e: Exception) {
                    print("exception $e")
                    throw e
                }
            }
        }
        // keep the jvm alive.
        // Without that, the CoroutineExceptionHandler on the root element would never be invoked
        Thread.sleep(2000)
    }

//    THIS WILL NOT WORK!
//    42  starting supervisor - main @coroutine#2
//    "coroutine#2":StandaloneCoroutine{Active}@396f6598
//    "coroutine#3":StandaloneCoroutine{Active}@305fd85d
//    "coroutine#4":StandaloneCoroutine{Active}@58c1670b
//    109  throwing ex1 - kotlinx.coroutines.DefaultExecutor @coroutine#3
//    130  exception Parent job is Cancelling - kotlinx.coroutines.DefaultExecutor @coroutine#4
//    131  CoroutineExceptionHandler1 got java.lang.Exception: ex1 - kotlinx.coroutines.DefaultExecutor @coroutine#4
//    2060  ended - main
//
//    The scope created by supervisorScope.launch uses a regular job so calling launch on it
//    will result a regular child
    fun supervisor3() {
        printJobInfo(scope)
        scope.launch(handler1) {
            val supervisorScope = CoroutineScope(coroutineContext + SupervisorJob())
            // this would result the same except that handler2 will catch the exception
            // scope.launch(SupervisorJob() + CoroutineName("supervisor") + handler2) {
            supervisorScope.launch(CoroutineName("supervisor")) {
                prnt("starting supervisor")
                printJobInfo(this)
                launch(CoroutineName("sub 1")) {
                    printJobInfo(this)
                    delay(50)
                    prnt("throwing ex1")
                    throw Exception("ex1")
                }
                launch(CoroutineName("sub 2")) {
                    try {
                        printJobInfo(this)
                        delay(1000)
                        // it's not canceled
                        prnt("finishing")
                    } catch (e: Exception) {
                        prnt("exception ${e.message}")
                        throw e
                    }

                }

            }
        }
        // keep the jvm alive.
        // Without that, the CoroutineExceptionHandler on the root element would never be invoked
        Thread.sleep(2000)
    }
}

private fun printJobInfo(scope: CoroutineScope) {
    println("this job: ${scope.coroutineContext.job}, parent job: ${scope.coroutineContext.job.parent}")
}

private fun callbackBasedMethod(callback: (String) -> Unit) {
    callback("important result")
}

suspend fun convertCallbacksToCoroutine(): String = suspendCoroutine { continuation ->
    callbackBasedMethod { result ->
        continuation.resumeWith(Result.success(result))
    }
}

fun main() {
    Coroutines().supervisor3()
    prnt("ended")
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")
