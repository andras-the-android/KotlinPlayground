// Write a function called "reconcileHelper" that processes two arrays of integers.
// Each array will have only distinct numbers (no repeats of the same integer in
// the same array), and the arrays are not sorted.
// Your job is to find out which numbers are in array 1, but not array 2,
// and which numbers are in array 2, but not in array 1.
// Your function should return a string formatted as so:
// "Numbers in array 1 that aren't in array 2:
// <num1> <num2> <num3>...
//
// Numbers in array 2 that aren't in array 1:
// <num1> <num2> <num3>...
// "
//
// Each list of numbers should be listed in ascending order (lowest to largest)
//
// So for instance, if I passed in:
// reconcileHelper([5, 3, 4], [4, 3, 10, 6])
//
// The function would return this multiline string:
// "Numbers in array 1 that aren't in array 2:
// 5
//
// Numbers in array 2 that aren't in array 1:
// 6 10
// "
//
// Notes:
// 1) You are allowed to use any standard library functions, but if it has a way of
//        doing this EXACT problem, please don't use it.
//        For example, don't use a function to subtract one array from another. That's too easy.
// 2) Try to make your solution fast so that it can handle over 1,000,000 elements in each array.
//    (Doing a linear search through array `b`, for every element in array `a` will work, but is too slow.)

//getNonContainingNumbers(arrayOf(5, 3, 4), arrayOf(4, 3, 10, 6)).forEach { println(it)}

fun main() {
    println(reconcileHelper(arrayOf(5, 3, 4), arrayOf(4, 3, 10, 6)))
}

fun reconcileHelper(a : Array<Int>, b: Array<Int>) : String {
    return "Numbers in array 1 that aren't in array 2:\n" +
            getNonContainingNumbers(a, b).sortAndFlatten() +
            "\nNumbers in array 2 that aren't in array 1:\n" +
            getNonContainingNumbers(b, a).sortAndFlatten()
}

fun getNonContainingNumbers(a : Array<Int>, b: Array<Int>): List<Int> {
    val set = HashSet(b.asList())
    val result = ArrayList<Int>()
    a.forEach {
        if (!set.contains(it)) result.add(it)
    }
    return result
}

fun List<Int>.sortAndFlatten(): String {
    val result = StringBuffer()
    val sortedList = sorted()
    for (i in sortedList.indices) {
        result.append(sortedList[i])
        if (i < sortedList.size - 1) result.append(", ")
    }
    return result.toString()
}
