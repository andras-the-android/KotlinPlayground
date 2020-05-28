package com.example.playground

import java.util.function.BiConsumer

class Lambda {


    private fun aaa(a: Int, l: (str: String, bl: Boolean) -> Unit) {
        if (a > 0) {
            l.invoke("bla", false)
            l("bla", false)
        }

    }

    private fun aaa2(a: Int, l: (bl: Boolean) -> Unit) {
        if (a > 0) {
            l.invoke(false)
            l(false)
        }

    }

    private fun  bbb(a: Int, c: Callback) {
        if (a > 0) {
            c.vvvv("bla", false)
        }
    }

    private fun  ccc(a: Int, o: BiConsumer<String, Boolean>) {
        if (a > 0) {
            o.accept("bla", false)
        }
    }

    fun something() {
        aaa(1) { _: String, bl: Boolean ->  print("hello $bl")}
        aaa(1) { _: String, bl: Boolean ->  print("hello $bl")}

        aaa2(1) { bl: Boolean ->  print("hello $bl")}
        aaa2(1) { bl ->  print("hello $bl")}
        aaa2(1) { print("hello $it")}

        //kotlin interface can`t be converted to lambda
        bbb(1, object: Callback {
            override fun vvvv(str: String, bl: Boolean) {
                print("hello $bl")
            }
        })

        ccc(1, BiConsumer { _, bl -> print("hello $bl") })
    }

    interface Callback {
        fun vvvv(str: String, bl: Boolean)
    }


}