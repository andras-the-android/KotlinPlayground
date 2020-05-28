package com.example.playground

fun main() {
    FunctionTypeWithReceiver().run()
}

class FunctionTypeWithReceiver {


//    aaa bbb
//    aaa bbb
    fun run() {
        transformStringReceiver("aaa") {
            "$this bbb"
        }

        transformStringNormal("aaa") {
            "$it bbb"
        }
    }

    private fun transformStringReceiver(s: String, action: String.() -> String) {
        log(s.action())
    }

    private fun transformStringNormal(s: String, action: (String) -> String) {
        log(action(s))
    }


}