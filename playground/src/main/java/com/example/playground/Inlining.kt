package com.example.playground

class Inlining {

    fun runInline() {
        doSgInlined(action1 = { 5 }) {
            log("actionInlined")
            // non-local return
            // inline function can return without label (= return from runInline())
            // this is why it's not a compile error that the lambda returns without result
            // doSg call will be skipped entirely
            return
        }
        doSg {
            log("action")
            // non-inlined function can only return from it's enclosing function
            return@doSg 5
        }
    }

    private inline fun doSgInlined(noinline action1: () -> Int, action2: () -> Int) {
        log("firstInlined")
        val result = action2()
        log("lastInlined $result")
        // this is legal because it's noinline
        emptyLambdaFunction(action1)
        // this is compile error, we can't leak the inlined function arguments
        // doSg(action2)
    }

    private fun doSg(action: () -> Int) {
        log("first")
        action()
        log("last")
    }

    private fun emptyLambdaFunction(action: () -> Int) {}

    fun runReified() {
        // or val someInt = returnMatchingTypeValue<Int>()
        val someInt: Int? = returnMatchingTypeValue()
        val someString: String? = returnMatchingTypeValue()
        val someBoolean: Boolean? = returnMatchingTypeValue()

        // int: 1212, string: Hello, boolean: null
        log("int: $someInt, string: $someString, boolean: $someBoolean")

        val someOldInt1 = returnMatchingTypeValueWithoutReified1(Int::class.java)
        val someOldInt2 = returnMatchingTypeValueWithoutReified2(6)
        log("oldInt1: $someOldInt1, oldInt2: $someOldInt2")
    }

    private inline fun <reified T> returnMatchingTypeValue(): T? {
        return when (T::class) {
            // smart cast won't work because we compare the classes, not the instances
            String::class -> "Hello" as T
            Int::class -> 1212 as T
            else -> null
        }
    }

    // this is how the previous function would look like without reified
    private fun <T> returnMatchingTypeValueWithoutReified1(clazz: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return when (clazz) {
            String::class.java -> "Hello" as T
            Int::class.java -> 1212 as T
            else -> null
        }
    }

    // this doesn't make any sense in this case but it's good for example
    private fun <T> returnMatchingTypeValueWithoutReified2(param: T): T? {
        if (param == null) return null
        @Suppress("UNCHECKED_CAST")
        // !! required because of a bug
        // https://youtrack.jetbrains.com/issue/KT-37878/No-Smart-cast-for-class-literal-reference-of-nullable-generic-type
        return when (param!!::class) {
            String::class -> "Hello" as T
            Int::class -> 1212 as T
            else -> null
        }
    }
}

fun main() {
    Inlining().apply {
        runInline()
        println()
        runReified()
    }
}
