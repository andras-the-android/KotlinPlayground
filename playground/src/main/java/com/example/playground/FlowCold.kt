@file:OptIn(FlowPreview::class)

package com.example.playground

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * all operators:
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/
 */
class FlowCold {

    class FlowBuilders {
        val fromOtherComplexTypes = emit1to3

        val simple = flowOf(1, 2, 3)

        val sophisticated = flow {
            emit(1)
            emit(2)
            emit(3)
        }
    }
    class IntermediateOperators {

        class Transformation {

            fun map() = runBlocking {
                emit1to3
                    .map { "the number is $it" }
                    .collect { println(it) }
            }

            // transform is a map that can emit multiple times per upstream item
            fun transform() = runBlocking {
                emit1to3
                    .transform {
                        emit("the number is $it")
                        emit("the number 10 times is ${it * 10}")
                    }
                    .collect { println(it) }
            }

//            emitting 1
//            collected 1
//            emitting 2
//            collected 3
//            emitting 3
//            collected 6

            // like reduce() but it's not a terminal operator and it emits the intermediate
            // values as well. See also runningFold()
            fun runningReduce() = runBlocking {
                emit1to3
                    .runningReduce { accumulator, value -> accumulator + value }
                    .collect { println("collected $it") }
            }

//            collected 10
//            emitting 1
//            collected 11
//            emitting 2
//            collected 13
//            emitting 3
//            collected 16
            fun runningFold() = runBlocking {
                emit1to3
                    .runningFold(10) { accumulator, value -> accumulator + value }
                    .collect { println("collected $it") }
            }

            // this is just an alternative name for runningFold()
            fun scan() = runBlocking {
                emit1to3
                    .scan(10) { accumulator, value -> accumulator + value }
                    .collect { println("collected $it") }
            }

        }

        class SizeLimiting {

            fun take() = runBlocking {
                emitForever
                    .take(2)
                    .collect { println(it) }
            }

//            emitting 0
//            emitting 1
//            397  collected 1
//            emitting 2
//            emitting 3
//            663  collected 3
//            emitting 4
//            emitting 5
//            917  collected 5
//            emitting 6
//            emitting 7
//            emitting 8
//            1179  collected 8
            fun sample() = runBlocking {
                val job = emitForever
                    .sample(250)
                    .onEach { prnt("collected $it") }
                    .launchIn(this)

                delay(1100)
                job.cancel()
            }
        }

    }
    class TerminalOperators {

        fun toNonFlowType() = runBlocking {
            // see also toSet(), toCollection()
            val list = emit1to3.toList()
        }

        // to ensure the the flow always returns only one value
        fun singleValue() = runBlocking {
            // see also firstOrNull(), last(), lastOrNull()
            val number = emitForever.first()
            print(number)
        }


        fun count() = runBlocking {
            // this will never finishes if we use on emitForever
            val count = emit1to3
                .count()
            print(count)
        }

        // accumulates the whole flow into one value. Here it returns 6
        fun reduce() = runBlocking {
            val count = emit1to3
                .reduce { accumulator, value -> accumulator + value }
            print(count)
        }

        // same as reduce but the accumulator has an initial value. Here it returns 16
        fun fold() = runBlocking {
            val count = emit1to3
                .fold(10) { accumulator, value -> accumulator + value }
            print(count)
        }
    }

    class Backpressure {

//        emitting 1
//        collecting 1
//        emitting 2
//        emitting 3
//        collected 1
//        collecting 2
//        collected 2
//        collecting 3
//        collected 3
        fun buffer() = runBlocking {
            emit1to3
                // it would work without that either but this is faster because without the buffer
                // the emitter would be blocked until the collection of the previous item finished
                .buffer()
                .collect {
                    println("collecting $it")
                    delay(250) // simulate a slow collector
                    println("collected $it")
                }
        }

//        emitting 1
//        collecting 1
//        emitting 2
//        emitting 3
//        collected 1
//        collecting 3
//        collected 3

//        this is the opposite of buffering. We drop the items that were emitted during the collection
        fun conflation() = runBlocking {
            emit1to3
                .conflate()
                .collect {
                    println("collecting $it")
                    delay(250) // simulate a slow collector
                    println("collected $it")
                }
        }

//        emitting 1
//        collecting 1
//        emitting 2
//        collecting 2
//        emitting 3
//        collecting 3
//        collected 3

//        similar to conflation but this time the collector is cancelled every time when a new emission
//        happened but processing of the previous item is not finished yet. It's clearly visible, that
//        the collection of 1 and 2 started but never finished. This behavior characterizes all the other
//        *Latest flow operators.
        fun collectLatest() = runBlocking {
            emit1to3
                .collectLatest {
                    println("collecting $it")
                    delay(250) // simulate a slow collector
                    println("collected $it")
                }
        }
    }

    class CompositionOfMultipleFlows {

//        1 -> one at 474 ms from start
//        2 -> two at 855 ms from start
//        3 -> three at 1270 ms from start

//        downstream emission only happens when a new item arrived from every upstream flow
//        here flow completed without the collection of the value 4
        fun zip() = runBlocking {
            val nums = (1..4).asFlow().onEach { delay(300) }
            val strs = flowOf("one", "two", "three").onEach { delay(400) }
            val startTime = System.currentTimeMillis()
            nums.zip(strs) { a, b -> "$a -> $b" }
                .collect { value ->
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }

//        1 -> one at 501 ms from start
//        2 -> one at 696 ms from start
//        2 -> two at 916 ms from start
//        3 -> two at 1009 ms from start
//        4 -> three at 1325 ms from start

//        downstream emission happens every time when a new value arrives from any flow.
        fun combine() = runBlocking {
            val nums = (1..4).asFlow().onEach { delay(300) }
            val strs = flowOf("one", "two", "three").onEach { delay(400) }
            val startTime = System.currentTimeMillis()
            nums.combine(strs) { a, b -> "$a -> $b" }
                .collect { value ->
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }

//        155  2 - main
//        172  1 - main
//        172  4 - main
//        172  3 - main
//        188  6 - main
//        189  5 - main

        // merges two or more streams of the same type (merging strategy is unclear)
        fun _merge() = runBlocking {
            val odd = flowOf(1, 3, 5).onEach { delay(10) }
            val even = flowOf(2, 4, 6).onEach { delay(4) }
            merge(odd, even)
                .collect { prnt(it.toString())}
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class Flattening {

        private val odd = flowOf(1, 3, 5).onEach { delay(10) }
        private val even = flowOf(2, 4, 6).onEach { delay(4) }

//        114  1
//        144  3
//        160  5
//        175  2
//        190  4
//        206  6

        // creates a single flow from a flow of flow (they must have the same T)
        // it works sequentially
        fun flattenConcat() = runBlocking {
            flowOf(odd, even).flattenConcat()
                .collect { prnt(it.toString())}
        }

//        134  2
//        145  1
//        146  4
//        146  3
//        164  6
//        164  5

        // like flattenConcat but it works concurrently. It's really similar to merge
        fun flattenMerge() = runBlocking {
            flowOf(odd, even).flattenMerge()
                .collect { prnt(it.toString())}
        }

//        120  transformed 1
//        143  transformed 3
//        159  transformed 5
//        174  transformed 2
//        189  transformed 4
//        205  transformed 6

        // it's a combination of flatten and map. It also creates a single stream from series of streams,
        // but here we can change the type of the value. It works sequentially
        fun flatMapConcat() = runBlocking {
            flowOf(odd, even).flatMapConcat { it.map { number -> "transformed $number" } }
                .collect { prnt(it)}
        }

        private fun getFlowOfItemsById(i: Int): Flow<String> = flow {
            emit("$i: First")
            delay(500) // wait 500 ms
            emit("$i: Second")
        }

//        239  1: First
//        764  1: Second
//        871  2: First
//        1387  2: Second
//        1497  3: First
//        2012  3: Second

        // a different approach from the Kotlin documentation
        fun flatMapConcat2() = runBlocking {
            (1..3).asFlow().onEach { delay(100) } // emit a number every 100 ms
                .flatMapConcat { getFlowOfItemsById(it) }
                .collect { value -> prnt(value) }
        }

//        317  1: First
//        412  2: First
//        524  3: First
//        821  1: Second
//        928  2: Second
//        1040  3: Second

        // like flatMapConcat but it precesses the stream concurrently
        fun flatMapMerge() = runBlocking {
            (1..3).asFlow().onEach { delay(100) } // emit a number every 100 ms
                .flatMapMerge { getFlowOfItemsById(it) }
                .collect { value -> prnt(value) }
        }


    }

//    start
//    emitting 1
//    on each 1
//    collected 1
//    emitting 2
//    on each 2
//    collected 2
//    emitting 3
//    on each 3
//    collected 3
//    completed
    fun lifecycle() = runBlocking {
        emit1to3
            .onStart { println("start") }
            .onEach { println("on each $it") }
            .onCompletion { println("completed") }
            .collect { println("collected $it") }

        println()

        emptyFlow<Int>()
            .onEmpty { println("on empty") }
            .collect()

    }

    fun cancellation() {
        runBlocking {
//            val job = launch {
//                emitForever
//                    .cancellable()
//                    .collect {
//                        println("collecting $it")
//                    }
//            }

            // this is the same the previous
            val job = emitForever
                // this is only required if the flow builder - unlike emitForever - doesn't call any cancellable function
                .cancellable()
                .onEach {
                    println("collecting $it")
                }
                .launchIn(this)

            delay(300)
            println("cancelling")
            job.cancel()
        }
    }

    private suspend fun functionOnAnotherScheduler() = withContext(Dispatchers.Default) {
        delay(100)
        "done"
    }

    fun runOnBackgroundThread() = runBlocking {
        flow {
            emit(functionOnAnotherScheduler())
        }
            .collect { println(it) }

        flow {
            delay(100)
            emit("done")
        }
            .flowOn(Dispatchers.Default)
            .collect { println(it) }

        // don't do that! it will throw an exception
        flow {
            withContext(Dispatchers.Default) {
                delay(100)
                emit("done")
            }
        }
//            .collect { println(it) }
    }
}

fun main() {
    FlowCold.IntermediateOperators.SizeLimiting().sample()
}

private val emitForever = flow {
    var counter = 0
    while (true) {
        delay(100)
        println("emitting $counter")
        emit(counter++)
    }
}

private val emit1to3 = flow {
    for (i in 1..3) {
        delay(100)
        println("emitting $i")
        emit(i)
    }
}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s")
