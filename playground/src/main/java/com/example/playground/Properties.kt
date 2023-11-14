package com.example.playground

class Properties {

    var someValue = 5

    // compile error
    // var lazyp: Int by lazy {
    val lazyp: Int by lazy {
        log("lazy")
        someValue * 2
    }

    val withoutInit get() = this.someValue * 2
    // compile error
    // var withoutInit get() = this.someValue * 2

    var initBlock = run {
        log("initBlock")
        5
    }

    var customAccessor: Int = 0
        get() {
            log("computed get $field")
            return someValue
        }
        set(value) {
            log("computed set $value")
            field = value
        }

}

fun main() {
    val p = Properties()
    log("initialized")
    p.customAccessor
    p.initBlock
    p.lazyp
    log("without init 1 ${p.withoutInit}")
    p.someValue = 10
    log("without init 2 ${p.withoutInit}")

}
