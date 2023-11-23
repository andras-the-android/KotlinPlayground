package com.example.playground

class ObjectExpressions {

    interface A {
        fun funFromA()
    }
    interface B

//    anonymous objects from scratch ------------------------------------------
//    object expression
//    must be private or local to expose the properties
    private val helloWorldOE = object {
        val hello = "Hello"
        var world = "World"
        // object expressions extend Any, so `override` is required on `toString()`
        override fun toString() = "$hello $world"
    }

//    object declaration
    object HelloWorldOD {
        val hello = "Hello"
        var world = "World"
        // object expressions extend Any, so `override` is required on `toString()`
        override fun toString() = "$hello $world"
    }

//    Object expressions are executed (and initialized) immediately, where they are used.
//    Object declarations are initialized lazily, when accessed for the first time.

//    anonymous objects from supertype ------------------------------------------

    private val listener = object: A {
        override fun funFromA() {
            println()
        }
    }

//    anonymous object as return value ------------------------------------------

    // The return type is Any; x is not accessible
    fun getObject() = object {
        val x: String = "x"
    }

    // The return type is the anonymous object; x is accessible
    private fun getObjectPrivate() = object {
        val x: String = "x"
    }

    // The return type is A; x is not accessible
    fun getObjectA() = object: A {
        override fun funFromA() {}
        val x: String = "x"
    }

    // The return type is B; funFromA() and x are not accessible
    fun getObjectB(): B = object: A, B { // explicit return type is required
        override fun funFromA() {}
        val x: String = "x"
    }

    fun run() {
        helloWorldOE.world = "World!"
        getObjectPrivate().x
    }

}
