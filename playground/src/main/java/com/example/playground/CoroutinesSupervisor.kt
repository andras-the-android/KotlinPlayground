package com.example.playground

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class CoroutinesSupervisor {

    private val scope = CoroutineScope(Dispatchers.Unconfined + CoroutineName("outer scope"))

    private val handler1 = CoroutineExceptionHandler { _, exception ->
        prnt("CoroutineExceptionHandler1 got $exception")
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

fun main() {
    CoroutinesSupervisor().supervisor3()
    prnt("ended")
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")
