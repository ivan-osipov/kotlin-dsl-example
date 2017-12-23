package kotlin_dsl

import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class Demo {

    @Test
    fun `demo test`() = schedule {
        data {
            startFrom("08:00")

            subjects("Russian",
                    "Literature",
                    "Algebra",
                    "Geometry")

            student {
                name = "Ivanov"
                subjectIndexes(0, 2)
            }

            student {
                name = "Petrov"
                subjectIndexes(1, 3)
            }

            teacher {
                subjectIndexes(0, 1)
                availability {
                    monday("08:00")
                    wednesday("09:00", "16:00")
                }
            }
            teacher {
                subjectIndexes(2, 3)
                availability {
                    thursday("08:00") + time("11:00") + time("14:00")
                }
            }

            // data { } // isn't compiled here because there is scope control with @DataContextMarker

        } assertions {
            for ((day, lesson, student, teacher) in scheduledEvents) {
                val teacherSchedule: Schedule = teacher.schedule
                teacherSchedule[day, lesson] shouldNotEqual null
                teacherSchedule[day, lesson]!!.student shouldEqual student

                val studentSchedule = student.schedule
                studentSchedule[day, lesson] shouldNotEqual null
                studentSchedule[day, lesson]!!.teacher shouldEqual teacher

                println("${DayOfWeek.of(day + 1)} (${LocalTime.of(8, 0).plusHours(lesson.toLong())}) T: ${teacher.name} S: ${student.name}")
            }
        }
    }
}