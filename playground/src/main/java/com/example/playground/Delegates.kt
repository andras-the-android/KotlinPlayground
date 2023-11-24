package com.example.playground

import kotlin.reflect.KProperty

class ClassDelegate {

    interface Delegate {

        val prop: String

        fun fun1()
        fun fun2()
    }

    class DelegateImpl: Delegate {

        override val prop = "prop in DelegateImpl"

        override fun fun1() {
            // this will not see the overridden prop in SomeClass
            log("fun1 in DelegateImpl. prop is $prop")
            // this won't call the overridden method in SomeClass
            fun2()
        }

        override fun fun2() {
            log("fun2 in DelegateImpl")
        }
    }

    class SomeClass(private val delegate: Delegate): Delegate by delegate {

        override val prop = "prop in SomeClass"

        override fun fun2() {
            // this will not work
            // super.fan2()
            log("fun2 in SomeClass")
            delegate.fun2()
        }
    }

    init {
        val someClass = SomeClass(DelegateImpl())
        someClass.fun1()
        someClass.fun2()
    }

}

class PropertyDelegate {

    private var p: String by Del()
    private var p1: String by Del()

    private var prop = "foo"
    private var propCopy: String by ::prop

    init {
        log(p)
        p1 = "pppp"
        log(p1)

        log(propCopy)
        propCopy = "bar"
        log(prop)
        prop = "foobar"
        log(propCopy)
    }

    class Del {

        private var backedValue = "aaaaa"

        operator fun getValue(thisRef: PropertyDelegate, property: KProperty<*>) : String {
            // com.example.playground.PropertyDelegate@3567135c, thank you for delegating 'p' to me!
            log("$thisRef, thank you for delegating '${property.name}' to me!")
            return backedValue
        }

        operator fun setValue(thisRef: PropertyDelegate, property: KProperty<*>, value: String) {
            // com.example.playground.PropertyDelegate@3567135c, thank you for delegating 'p1' to me!
            log("$value has been assigned to '${property.name}' in $thisRef.")
            backedValue = value
        }
    }
}

fun main() {
    PropertyDelegate()
    //ClassDelegate()
}
