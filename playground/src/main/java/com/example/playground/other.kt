package com.example.playground

import kotlinx.coroutines.runBlocking


fun main() = runBlocking {

}

private val startTimestamp = System.currentTimeMillis()

/**
 * Use this jvm parameter to display the full name of the coroutine
 * -Dkotlinx.coroutines.debug
 */
private fun prnt(s: String) = println("${System.currentTimeMillis() - startTimestamp}  $s - ${Thread.currentThread().name}")
