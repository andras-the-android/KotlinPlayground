package com.example.playground

import java.lang.ref.WeakReference

fun main() {
    GarbageCollection().run()
}

class GarbageCollection {

    private val repository = Repository()

    class Repository() {

        private var listener : WeakReference<() -> Unit>? = null

        fun setListener(listener: () -> Unit) {
            this.listener = WeakReference(listener)
        }

        fun doSg() {
            Thread {
                Thread.sleep(2000)
                this.listener?.get()?.invoke() ?: log("missing listener")
            }.start()
        }
    }

    class DisposableClass(private val repository: Repository) {

        fun callRepository() {
            repository.setListener { log("task finished"); handleResult() }
            repository.doSg()
            log("method called")
        }

        fun handleResult() {
            log("result handled")
        }

        protected fun finalize() {
            log("finalized")
        }

    }

    fun run() {
        DisposableClass(repository).callRepository()
        System.gc()
    }

    fun handleResult() {
        log("result handled")
    }



}