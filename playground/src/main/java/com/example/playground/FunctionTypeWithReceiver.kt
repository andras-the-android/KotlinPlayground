package com.example.playground

class Decorator(
    private val strToDecorate: String
) {

    fun printDecorated1(createPattern: (strToDecorate: String, delimiter: Char) -> String) {
        val result = createPattern(strToDecorate, DELIMITER)
        println(result)
    }

    fun printDecorated2(createPattern: String.(delimiter: Char) -> String) {
        val result = createPattern(strToDecorate, DELIMITER) //or strToDecorate.block(DELIMITER)
        println(result)
    }

    companion object {
        private const val DELIMITER = '-'
    }
}

/**
 * see also: Type Safe Builders
 * https://kotlinlang.org/docs/type-safe-builders.html
 */

fun main() {
    val d = Decorator("Function type with receiver")

    // normal function parameter with two arguments
    // *** - Function type with receiver - ***
    d.printDecorated1 { strToDecorate, delimiter ->
        return@printDecorated1 "*** $delimiter $strToDecorate $delimiter ***"
    }

    // same functionality with receiver type. strToDecorate is referenced by this keyword
    // ### - Function type with receiver - ###
    d.printDecorated2 { delimiter ->
        return@printDecorated2 "### $delimiter $this $delimiter ###"
    }
}
