package com.example.playground

import kotlin.Nothing

/**
 * Nothing return type represents a function that won't return ever
 * Nothing is a subtype of every class this is why it can replace any class
 * TODO() and emptyList()/emptySet()/emptyMap() also uses Nothing
 */
class Nothing {

    private var nullableData: String? = null

    private fun failure(): Nothing {
        throw Exception()
    }

    private fun doSg(onError: (errorCode: Int) -> Nothing) {
        // do something that fails at some point
        onError(5)
    }

    fun start() {
        val nonNullData = nullableData ?: failure()

        doSg() { errorCode ->
            // there is no other choice here than throwing an exception,
            // but the solution let us to customize it's type
            throw Exception("$errorCode")
        }

        // the compiler notifies that the execution will be interrupted
        val a = failure()
    }
}
