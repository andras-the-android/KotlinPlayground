package com.example.playground

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoroutinesExceptions {
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

    // When an exception reaches the root scope without being caught, and the scope doesn't have an
    // exception handler, it will be swallowed. However, this doesn't mean that it can't cause trouble.
    // Different systems handle uncaught exceptions differently. This will run properly on local jvm
    // (only an error will be displayed on the console) but on Android it crashes the entire app uncatchably.
    fun exceptionPropagation() {
        scope.launch {
            prnt("launching coroutine")
            throw Exception("boom")
        }
    }

    // This will behave the same like the CoroutineExceptionHandler wouldn't be there
    fun exceptionPropagation2() {
        scope.launch(CoroutineExceptionHandler { _, throwable -> throw throwable }) {
            prnt("launching coroutine")
            throw Exception("boom")
        }
    }

}

fun main() {
    CoroutinesExceptions().exception()
    prnt("ended")
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")
