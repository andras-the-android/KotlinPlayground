package com.example.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class CoroutinesCancellation {

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
        yield() // context.ensureActive()
        // delay is cancellable by itself so it's not good for this demonstration
        // unless we call it from this special context
        withContext(NonCancellable) {
            delay(500)
        }
    }
}

fun main() {
    CoroutinesCancellation().cancellation()
    prnt("ended")
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")

