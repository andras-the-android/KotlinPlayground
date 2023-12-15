package com.example.playground

/**
 * based on https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/
 */
class Strings {

    fun byToWithIndexed() {
        val destination = mutableMapOf('x' to 256)
        printAll(
            // turns a cs into a map
            "aaab".associate { char -> char to char.code }, // {a=97, b=98}

            // same but it copies the result into a destination variable as well
            "aaab".associateTo(destination) { char -> char to char.code }, // {x=256, a=97, b=98}
            destination, // {x=256, a=97, b=98}

            // same but it has separate methods for generating key and value
            "aaab".associateBy(
                keySelector = { it },
                valueTransform = { it.code }
            ), // {a=97, b=98}

            // same but the the keys will be the characters, we can only calculate the value
            "aaab".associateWith { char -> char.code }, // {a=97, b=98}
        )

        printAll(
            "abc123def".filter { char -> char.isLetter() }, // abcdef
            // same but it has also an index parameter in the lambda
            "abc123def".filterIndexed { index, char -> char.isLetter() }, // abcdef
        )


    }

    fun comparsion() {
        printAll(
            // returns this value or the minimum value if this value is smaller
            "bbb".coerceAtLeast("ccc"), // ccc
            "bbb".coerceAtLeast("aaa"), // bbb

            // opposite of coerceAtLeast
            "bbb".coerceAtMost("ccc"), // bbb
            "bbb".coerceAtMost("aaa"), // aaa

            // combo of the previous two
            "aaa".coerceIn("bbb".."ddd"), // bbb
            "ccc".coerceIn("bbb".."ddd"), // ccc
            "eee".coerceIn("bbb".."ddd"), // ddd
        )

        printAll(
            "aaab".commonPrefixWith("aaac"), // aaa
            "aaab".commonPrefixWith("bbbb"), // <empty cs>
            "baaa".commonSuffixWith("caaa"), // aaa
        )

        printAll(
            // comparison between different cs implementations
            "aaa".equals(StringBuilder("aaa")), // false
            "aaa".contentEquals(StringBuilder("aaa")) // true
        )

    }

    fun filtering() {
        printAll(
            "abc123def".filter { char -> char.isLetter() }, // abcdef
            "abc123def".filterNot { char -> char.isLetter() }, // 123
        )
    }

    fun finding() {
        printAll(
            "abc"[3], // IndexOutOfBoundException
            "abc".elementAt(3), // IndexOutOfBoundException
            "abc".elementAtOrElse(3) { it.digitToChar() }, // 3
            "abc".elementAtOrNull(3), // null
        )

    }

    fun iterating() {

        printAll(
            "aaab".all { it == 'a' }, // false
            "aaaa".all { it == 'a' }, // true
        )

        printAll(
            "aaab".any { it == 'b' }, // true
            "aaaa".any { it == 'b' }, // false
            "aaaa".any(), // true
            "".any(), // false
        )
    }

    fun transformation() {
        printAll(
            // turns a cs into a map
            "aaab".associate { char -> char to char.code }, // {a=97, b=98}
        )

        printAll(
            // turns a cs into a map of arrays
            "aaab".groupBy { char -> char.code }, // {97=[a, a, a], 98=[b]}
            "aaab".groupBy(
                keySelector = { char ->  char.code},
                valueTransform = { char -> char.uppercaseChar()}
            ), // {97=[A, A, A], 98=[B]}

        )

        printAll(
            // [a, aa, b, bb, c, cc, d, dd]
            "abcd".flatMap { listOf(it, "$it$it") }
        )
    }

    fun splitting() {
        printAll(
            // splits cs into a size sized chunks and optionally makes a transformation
            "hello_world".chunked(
                size = 3,
                transform = { chunk -> chunk.toString().uppercase()  }
            ) // [HEL, LO_, WOR, LD]
        )

        printAll(
            // cuts a particular portion from the start or the end and returns the remainder
            "..abcdefg..".drop(3), // bcdefg..
            "..abcdefg..".dropLast(3), // ..abcdef
            "..abcdefg..".dropWhile { !it.isLetter() }, // abcdefg..
            "..abcdefg..".dropLastWhile { !it.isLetter() }, // ..abcdefg
        )
    }
}

fun main() {
    Strings().transformation()
    printAll(
        // TODO continue from slice
    )
}

private fun printAll(vararg items: Any?) {
    items.forEach {
        println(it)
    }
}
