package com.example.playground

var loggerAdapter: ((String) -> Unit)? = null

fun log(s: String) {
    loggerAdapter?.invoke(s) ?: println(s)
}