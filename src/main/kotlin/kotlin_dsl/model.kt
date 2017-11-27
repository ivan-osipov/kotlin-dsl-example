package kotlin_dsl

import java.util.*
import kotlin.collections.HashSet

typealias AvailabilityTable = Matrix<Boolean>

typealias Schedule = Matrix<Event?>

val DAYS_PER_WEEK = 7

val LESSONS_PER_DAY = 10

open class Identifiable(open var id: UUID = UUID.randomUUID()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as? Identifiable ?: return false
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

open class Resource : Identifiable() {
    var name: String? = null
    val schedule = Schedule(DAYS_PER_WEEK, LESSONS_PER_DAY, { _, _ -> null })
}

open class Subject(val name: String) : Identifiable()

open class Student : Resource() {
    val subjectRequirements = HashSet<Subject>()
}

open class Teacher : Resource() {
    var subjects = HashSet<Subject>()
    val availability = AvailabilityTable(DAYS_PER_WEEK, LESSONS_PER_DAY, { _, _ -> false })
}

data class Event(val day: Int, val lesson: Int, val student: Student, val teacher: Teacher)
