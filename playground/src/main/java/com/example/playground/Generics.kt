package com.example.playground

class Generics {

    open class Parent

    open class Basic: Parent()

    open class Child: Basic()


    class Out {
        // declaration-site variance
        class ClassOut<out T> { // T or it's descendants (covariance)

            // must be private
            private var value: T? = null

            fun getT() : T? {
                return value
            }

//        compile error
//        fun setT(t: T) {
//           value = t
//        }

//        prohibiting setters doesn't mean that this object is immutable.
//        these limitations are only for ensuring type safety
            fun clear() {
                value = null
            }
        }

        val classOut: ClassOut<Basic> = ClassOut<Child>()

        // this is why in parameters are prohibited
        fun testOut() {
            val classOutChild: ClassOut<Child> = ClassOut()
            val classOutBasic: ClassOut<Basic> = classOutChild
            // classOutBasic.setT(Basic()) ClassCastException
        }
    }


    class In {
        class ClassIn<in T> { // T or it's ancestors (contravariance)

            // must be private
            private var value: T? = null

//        compile error
//        fun getT() : T? {
//            return value
//        }

            fun setT(t: T) {
                value = t
            }
        }

        // this is why out parameters are prohibited
        fun testIn() {
            val classInParent: ClassIn<Parent> = ClassIn<Parent>().apply { setT(Parent()) }
            val classInBasic: ClassIn<Basic> = classInParent
            // classInBasic.getT() ClassCastException
        }
    }

    class ClassInOut<in T, out F>

    val classInOut: ClassInOut<Basic, Basic> = ClassInOut<Parent, Child>()

    class TypeProjection() {

//        This class can be neither co- nor contravariant in T
        class ClassStrict<T> {

            fun getT() : T? {
                return null
            }

            fun setT(t: T) {

            }
        }

        private fun funOut(cs: ClassStrict<out Basic>) {
            cs.getT()
//            out parameters are read-only
//            cs.setT(Basic())
        }

        private fun funIn(dd: ClassStrict<in Basic>) {
            // in parameters are read-write
            dd.getT()
            dd.setT(Basic())
        }

        init {
//          type projections (Use-site variance) makes invariant classes behave like co- or contravariant

// compile error
//        funOut(ClassStrict<Parent>())
            funOut(ClassStrict<Child>())
            funIn(ClassStrict<Parent>())
// compile error
//        funIn(ClassStrict<Child>())
        }
    }


}
