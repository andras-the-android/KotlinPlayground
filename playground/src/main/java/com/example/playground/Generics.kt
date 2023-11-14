package com.example.playground

class Generics {

    open class Base

    open class Derived: Base()

    open class EvenMoreDerived: Derived()


    //-------------------------------------------------

    class A<out T> {

        fun getT() : T? {
            return null
        }

// compile error
//        fun setT(t: T) {
//
//        }

    }

    val a: A<Derived> = A<EvenMoreDerived>()


    //-------------------------------------------------



    class B<in T> {

// compile error
//        fun getT() : T? {
//            return null
//        }

        fun setT(t: T) {

        }

    }

    init {
        val b: B<Derived> = B<Base>()
        b.setT(EvenMoreDerived())
    }

    //-------------------------------------------------


    class C<in T, out F>

    val c: C<Derived, Derived> = C<Base, EvenMoreDerived>()

    //-------------------------------------------------
    //type projection

    class D<T> {

        fun getT() : T? {
            return null
        }

        fun setT(t: T) {

        }
    }

    fun d1(dd: D<out Derived>) {
        dd.getT()
//        dd.setT(Derived())
    }

    fun d2(dd: D<in Derived>) {
        dd.getT()
        dd.setT(Derived())
    }

    init {
// compile error
//        d1(D<Base>())
        d1(D<EvenMoreDerived>())
        d2(D<Base>())
// compile error
//        d2(D<EvenMoreDerived>())
    }
}
