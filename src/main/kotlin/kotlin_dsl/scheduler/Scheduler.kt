package kotlin_dsl.scheduler

import kotlin_dsl.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Scheduler {
    fun schedule(dataSet: DataSet) : SchedulingResults {
        val students = dataSet.students
        val teachers = dataSet.teachers
        val teachersBySubjects = HashMap<Subject, MutableSet<Teacher>>()
        val tasks = students.flatMap { student -> student.subjectRequirements.map { subject -> student to subject } }
        Collections.shuffle(tasks)
        for (teacher in teachers) {
            for (subject in teacher.subjects) {
                teachersBySubjects.computeIfAbsent(subject) { HashSet() }
                teachersBySubjects[subject]!!.add(teacher)
            }
        }
        val events = HashSet<Event>()
        for ((student, subject) in tasks) {
            val teacher = teachersBySubjects[subject]!!.toList().randomOne()
            val teacherAvailability = teacher.availability
            val studentSchedule = student.schedule
            val teacherSchedule = teacher.schedule
            allocate(teacherAvailability, studentSchedule, teacherSchedule, student, teacher, events)
        }

        return SchedulingResults(events)
    }

    private fun allocate(teacherAvailability: AvailabilityTable, studentSchedule: Schedule, teacherSchedule: Schedule, student: Student, teacher: Teacher, events: HashSet<Event>) {
        for (i in 0 until teacherAvailability.width) {
            for (j in 0 until teacherAvailability.height) {
                if (teacherAvailability[i, j] && studentSchedule[i, j] == null && teacherSchedule[i, j] == null) {
                    Event(i, j, student, teacher).apply {
                        studentSchedule[i, j] = this
                        teacherSchedule[i, j] = this
                        events.add(this)
                    }
                    return
                }
            }
        }
    }

    private fun <T> List<T>.randomOne() : T = this[Random().nextInt(this.size)]

}