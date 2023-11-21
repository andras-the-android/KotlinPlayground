package com.example.playground


/**
 * functional or sam (single abstract method) interfaces
 * https://kotlinlang.org/docs/fun-interfaces.html
 */
interface MyInterface {

    val prop: Int // abstract, it's not possible to assign a value

    var propertyWithImplementation: String
        get() = SOME_VALUE
        set(value) {}

    fun foo() {
        print(prop)
    }

    companion object {
        const val SOME_VALUE = "foo"
    }
}

class MyInterfaceImpl: MyInterface {

    // this is mandatory to override
    override val prop: Int = 5

    // it's not necessary to override the custom accessors
    override var propertyWithImplementation: String = "hello"

    // this is not necessary either but because of the default implementation we need to call super
    override fun foo() {
        super.foo()
        println()
    }

}


