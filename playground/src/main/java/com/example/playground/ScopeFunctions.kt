package com.example.playground

class ScopeFunctions {

    data class Dto(var s: String = "", var n: Int = 10)

    init {

        //returns the context object; reference by this
        var dto: Dto? = null
        dto = Dto().apply {
            s = "foo"
            n = 13
        }

        //returns the context object; reference by it
        dto = Dto().also {
            it.s = "foo"
            it.n = 13
        }

        dto = Dto("foo", 13)
        var s: String? = null

        //returns the lambda expression; reference by this
        s = dto.run {
            "$s: $n"
        }

        //returns the lambda expression; reference by it
        s = dto.let {
            "${it.s}: ${it.n}"
        }

        //non-extension function; returns the lambda expression; reference by this
        s = with(dto) {
            "$s: $n"
        }
    }
}