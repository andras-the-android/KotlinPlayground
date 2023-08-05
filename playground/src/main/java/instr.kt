fun main() {
//    println(calculateGrade(
//        arrayOf(1.0, 2.0),
//        arrayOf(arrayOf(10.0, 10.0), arrayOf(10.0, 10.0)),
//        arrayOf(10.0, 0.0, 8.0, 0.0)
//    ))

//    println(calculateGrade(
//        arrayOf(2.0, 3.0, 5.0),
//        arrayOf(arrayOf(1.0, 1.0), arrayOf(2.0, 3.0), arrayOf(5.0, 5.0)),
//        arrayOf(0.0, 0.0, 1.0, 2.0, 4.0, 5.0)
//    ))

//    println(calculateGrade(
//        arrayOf(1.0, 2.0),
//        arrayOf(arrayOf(10.0), arrayOf(20.0)),
//        arrayOf(4.0, 8.0)
//    ))

//    println(calculateGrade(
//        arrayOf(2.0),
//        arrayOf(arrayOf(2.0, 2.0)),
//        arrayOf(1.0, 1.0)
//    ))

}

fun calculateGrade(weights: Array<Double>, assignments: Array<Array<Double>>, submissions: Array<Double>): Double {
    var gradeSummary: Double  = 0.0
    var submissionIndex = 0
    for (groupIndex in assignments.indices) {
        val weight = weights[groupIndex]
        var assignmentMaxPoints = 0.0
        var assignmentAchievedPoints = 0.0
        for (assignmentIndex in assignments[groupIndex].indices) {
            assignmentMaxPoints += assignments[groupIndex][assignmentIndex]
            assignmentAchievedPoints += submissions[submissionIndex++]
        }
        gradeSummary += assignmentAchievedPoints / assignmentMaxPoints * weight
    }
    return gradeSummary
}

fun calculateGradeWrong(weights: Array<Double>, assignments: Array<Array<Double>>, submissions: Array<Double>): Double {
    var gradeSummary: Double  = 0.0
    var submissionIndex = 0
    for (groupIndex in assignments.indices) {
        val weight = weights[groupIndex]
        for (assignmentIndex in assignments[groupIndex].indices) {
            val assignmentMaxPoints = assignments[groupIndex][assignmentIndex]
            val assignmentAchievedPoints = submissions[submissionIndex++]
            val weigtedGrade = assignmentAchievedPoints / assignmentMaxPoints * weight
//            println(weigtedGrade)
            gradeSummary += weigtedGrade
        }
    }
    return gradeSummary
}

//data class Group(
//    val weight: Double,
//)
//
//data class Submission(
//    val maxPoints: Double,
//    val achieved: Double
//)
//1 0 1.6 0
//0 0 1.5 2 4 5
//calculateGrade(weights = [ 1, 2 ], assignments = [ [ 10, 10 ], [ 10, 10 ], submissions [ 10, 0, 8, 0])
//grade1 = (10 / 10) * 1 = 1
//grade2 = (0 / 10) * 1 = 0
//grade3 = (8 / 10) * 2 = 1.6
//grade3 = (0 / 10) * 2 = 0
//1 + 0 + 1.6 + 0 = 2.6
//
//groupGrade1 = (10 + 0) / (10 + 10) * 1 = 0.5
//groupGrade2 = (8 + 0) / (10 + 10) * 2 = 0.8
//0.5 + 0.8 = 1.3



