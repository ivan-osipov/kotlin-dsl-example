package kotlin_dsl

import kotlin_dsl.scheduler.Scheduler
import kotlin_dsl.scheduler.SchedulingResults
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object schedule {

    operator fun invoke(init: SchedulingContext.() -> Unit) = SchedulingContext().init()

}

@MyCustomDslMarker
class SchedulingContext {
    fun data(init: DataContext.() -> Unit): SchedulingResults {
        val context = DataContext().apply(init)
        val dataSet = context.buildDataSet()
        val scheduler = Scheduler()
        return scheduler.schedule(dataSet)
    }
}

@MyCustomDslMarker
class DataContext {

    private val subjects = ArrayList<Subject>()

    private val students = ArrayList<Student>()

    private val teachers = ArrayList<Teacher>()

    private var startTime = LocalTime.of(8, 0)

    var lessonIntervalInMinutes = 60

    fun startFrom(time: String) {
        startTime = LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(time))
    }

    fun subjects(vararg names: String) {
        names.mapTo(subjects) { Subject(it) }
    }

    fun student(init: Student.() -> Unit) = Student().apply {
        name = "Student " + students.size
        init()
        students.add(this)
    }

    fun teacher(init: Teacher.() -> Unit) = Teacher().apply {
        name = "Teacher " + teachers.size
        init()
        teachers.add(this)
    }

    fun buildDataSet() = DataSet().apply {
        teachers.addAll(this@DataContext.teachers)
        students.addAll(this@DataContext.students)
    }

    fun Student.subjectIndexes(vararg indexes: Int) {
        this.subjectRequirements.addAll(indexes.map { subjects[it] })
    }

    fun Teacher.subjectIndexes(vararg indexes: Int) {
        this.subjects.addAll(indexes.map { this@DataContext.subjects[it] })
    }

    fun Teacher.availability(init: AvailabilityTable.() -> Unit) = this.availability.init()

    fun AvailabilityTable.monday(from: String, to: String? = null) = day(DayOfWeek.MONDAY, from, to)

    fun AvailabilityTable.tuesday(from: String, to: String? = null) = day(DayOfWeek.TUESDAY, from, to)

    fun AvailabilityTable.wednesday(from: String, to: String? = null) = day(DayOfWeek.WEDNESDAY, from, to)

    fun AvailabilityTable.thursday(from: String, to: String? = null) = day(DayOfWeek.THURSDAY, from, to)

    fun AvailabilityTable.friday(from: String, to: String? = null) = day(DayOfWeek.FRIDAY, from, to)

    fun AvailabilityTable.saturday(from: String, to: String? = null) = day(DayOfWeek.SATURDAY, from, to)

    fun AvailabilityTable.sunday(from: String, to: String? = null) = day(DayOfWeek.SUNDAY, from, to)

    fun AvailabilityTable.day(dayOfWeek: DayOfWeek, from: String, to: String? = null): DayPointer {
        for (lessonIndex in time(from, to)) {
            this[dayOfWeek.ordinal, lessonIndex] = true
        }
        return DayPointer(this, dayOfWeek)
    }

    fun time(from: String, to: String? = null): IntRange {
        val fromTime = LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(from))
        val toTime = to?.let { LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(to)) } ?: fromTime.plusHours(1)
        val secondsFromStart = fromTime.toSecondOfDay() - startTime.toSecondOfDay()
        val startLessonIndex = secondsFromStart / (lessonIntervalInMinutes * 60)
        val secondsToEnd = toTime.toSecondOfDay() - startTime.toSecondOfDay()
        val endLessonIndex = secondsToEnd / (lessonIntervalInMinutes * 60) - 1
        return startLessonIndex..endLessonIndex
    }

}

data class DayPointer(val availabilityTable: AvailabilityTable,
                      val dayOfWeek: DayOfWeek) {
    operator fun plus(lessonIndexRange: IntRange): DayPointer {
        val (table, dayOfWeek) = this
        for (lessonIndex in lessonIndexRange) {
            table[dayOfWeek.ordinal, lessonIndex] = true
        }
        return this
    }
}

class AssertionsContext(val scheduledEvents: Set<Event>)

infix fun SchedulingResults.assertions(init: AssertionsContext.() -> Unit) = AssertionsContext(this.scheduledEvents).init()

infix fun <T : Any?> T.shouldNotEqual(expected: T) {
    Assert.assertThat(this, not(equalTo(expected)))
}

infix fun <T : Any?> T.shouldEqual(expected: T) {
    Assert.assertThat(this, equalTo(expected))
}