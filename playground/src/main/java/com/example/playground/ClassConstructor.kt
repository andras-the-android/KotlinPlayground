package com.example.playground

class SomeClass(name: String) {
    private val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints $name")
    }

    private val secondProperty = "Second property: ${name.length}".also(::println)

    init {
        println("Second initializer block that prints $secondProperty")
    }

    private var id: Int? = null

//    First property: xxxx
//    First initializer block that prints xxxx
//    Second property: 4
//    Second initializer block that prints Second property: 4
//    Secondary constructor
    constructor(name: String, id: Int) : this(name) {
        println("Secondary constructor")
        this.id = id
    }
}

class OnlySecondaryConstructor {

    init {
        println("Init block")
    }

//    Init block
//    Constructor 5
    constructor(i: Int) {
        println("Constructor $i")
    }
}

fun main() {
    SomeClass("xxxx", 123)
    //OnlySecondaryConstructor(5)
}
