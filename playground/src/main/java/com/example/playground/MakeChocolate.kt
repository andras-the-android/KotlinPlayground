package com.instructure.test

import java.lang.Integer.min

/*
 We want make a package of goal kilos of chocolate.
 We have small bars (1 kilo each) and big bars (5 kilos each).
 Return the number of small bars to use, assuming we always use big bars before small bars.
 Return -1 if it can't be done.

 makeChocolate(4, 1, 9)     → 4
 makeChocolate(4, 1, 10)    → -1
 makeChocolate(4, 2, 7)     → 2

 You can run your solution by pressing: Ctrl + Shift + R or pressing the play icon on the TouchBar
*/

/* YOUR SOLUTION */
fun makeChocolate(small: Int, big: Int, goal: Int): Int {
    val necessaryBigOnes = goal / 5
    val necessarySmallOnes = goal - (necessaryBigOnes * 5)

    if (necessaryBigOnes > big) {
        val necessarySmallOnes = goal - (big * 5)
        if (necessarySmallOnes > small) return -1

    }

    if (necessarySmallOnes > small) return -1
    return necessarySmallOnes
}


fun makeChocolateee(small: Int, big: Int, goal: Int): Int {
    val bigNeeded = goal / 5
    val bigActual = big
    val finalBig = min(bigNeeded, bigActual)
    val smallNeeded = goal - finalBig * 5
    return if (smallNeeded <= small) smallNeeded else -1
}

/* "TEST ENVIRONMENT" */
data class TestCase(
    val small: Int,
    val big: Int,
    val goal: Int,
    val expected: Int
)

fun TestCase.result(): Int {
    return makeChocolate(small, big, goal)
}

fun TestCase.run(): Boolean {
    return expected == result()
}

fun TestCase.description(): String {
    return "($small, $big, $goal)"
}

val testCases = arrayOf(
    TestCase(small = 4, big = 1, goal = 9, expected = 4),
    TestCase(small = 4, big = 1, goal = 10, expected = -1),
    TestCase(small = 4, big = 1, goal = 7, expected = 2),
    TestCase(small = 6, big = 2, goal = 7, expected = 2),
    TestCase(small = 4, big = 1, goal = 5, expected = 0),
    TestCase(small = 4, big = 1, goal = 4, expected = 4),
    TestCase(small = 5, big = 4, goal = 9, expected = 4),
    TestCase(small = 9, big = 3, goal = 18, expected = 3),
    TestCase(small = 3, big = 1, goal = 9, expected = -1),
    TestCase(small = 1, big = 2, goal = 7, expected = -1),
    TestCase(small = 1, big = 2, goal = 6, expected = 1),
    TestCase(small = 1, big = 2, goal = 5, expected = 0),
    TestCase(small = 6, big = 1, goal = 10, expected = 5),
    TestCase(small = 6, big = 1, goal = 11, expected = 6),
    TestCase(small = 6, big = 1, goal = 12, expected = -1),
    TestCase(small = 6, big = 1, goal = 13, expected = -1),
    TestCase(small = 6, big = 2, goal = 10, expected = 0),
    TestCase(small = 6, big = 2, goal = 11, expected = 1),
    TestCase(small = 6, big = 2, goal = 12, expected = 2),
    TestCase(small = 60, big = 100, goal = 550, expected = 50),
    TestCase(small = 1000, big = 1000000, goal = 5000006, expected = 6),
    TestCase(small = 7, big = 1, goal = 12, expected = 7),
    TestCase(small = 7, big = 1, goal = 13, expected = -1),
    TestCase(small = 7, big = 2, goal = 13, expected = 3)
)


fun main() {
    val solutionSucceeded = testCases.map { it.run() }.reduce { acc, b -> acc && b }
    println(if (solutionSucceeded) "SUCCESS" else "FAIL")
    if (!solutionSucceeded) {
        testCases
            .filter { !it.run() }
            .forEach {
                println("Testcase ${it.description()} FAILED, should be ${it.expected}, makeChocolate resulted in ${it.result()}")
            }
    }
}

