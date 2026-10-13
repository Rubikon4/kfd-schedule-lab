package schedule

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionTest {
    @Test
    fun createLectureSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "",
                place = "place",
                teachersRaw = expectedTeachers,
            )
        }
    }

    @Test
    fun createLectureFailIncorrectDate() {
        val incorrectDate = LocalDate.of(10000, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = incorrectDate,
                topic = "topic",
                place = "place",
                teachersRaw = expectedTeachers,
            )
        }
    }

    @Test
    fun createLectureSuccessSingleTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureSuccessMinimumYear() {
        val expectedDate = LocalDate.of(1, 1, 1)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureSuccessMaximumYear() {
        val expectedDate = LocalDate.of(9999, 12, 31)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureSuccessInnerSpaces() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("Ivan Petrov")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "Kotlin basics",
            place = "Room 312",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("Kotlin basics", session.topic)
        assertEquals("Room 312", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureSuccessDashTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "-",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("-", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createLectureFailZeroYear() {
        val expectedDate = LocalDate.of(0, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailWithoutTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }
    }

    @Test
    fun createLectureFailBlankTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "   ",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailOuterSpaceTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = " topic ",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailControlCharacterTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic\tname",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailWithoutPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailBlankPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "   ",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailDashPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "-",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailOuterSpacePlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place ",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailControlCharacterPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "Room\u0000 312",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailEmptyTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", ""),
            )
        }
    }

    @Test
    fun createLectureFailBlankTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "   "),
            )
        }
    }

    @Test
    fun createLectureFailDashTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "-"),
            )
        }
    }

    @Test
    fun createLectureFailOuterSpaceTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", " teacher2"),
            )
        }
    }

    @Test
    fun createLectureFailControlCharacterTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher\nname"),
            )
        }
    }

    @Test
    fun createLectureFailTwinTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher"),
            )
        }
    }

    @Test
    fun changeOriginalTeachersWithoutChangingLecture() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val teachers = mutableListOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = teachers,
        )

        teachers.clear()

        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun changeReturnedTeachersWithoutChangingLecture() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Lecture(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        val returnedTeachers = session.teachers as? MutableList<String>
        if (returnedTeachers != null) {
            try {
                returnedTeachers.clear()
            } catch (_: UnsupportedOperationException) {
            }
        }

        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createPracticeSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher")
        val session = Session.Practice(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createPracticeSuccessMinimumYear() {
        val expectedDate = LocalDate.of(1, 1, 1)
        val session = Session.Practice(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = listOf("teacher"),
        )

        assertEquals(1, session.date.year)
    }

    @Test
    fun createPracticeSuccessMaximumYear() {
        val expectedDate = LocalDate.of(9999, 12, 31)
        val session = Session.Practice(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = listOf("teacher"),
        )

        assertEquals(9999, session.date.year)
    }

    @Test
    fun createPracticeFailZeroYear() {
        val incorrectDate = LocalDate.of(0, 1, 1)
        assertFailsWith<InvalidArgumentsException> {
            Session.Practice(
                date = incorrectDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher"),
            )
        }
    }

    @Test
    fun createPracticeFailWithoutTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Practice(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }
    }

    @Test
    fun createPracticeFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Practice(
                date = expectedDate,
                topic = "",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createPracticeFailIncorrectDate() {
        val expectedDate = LocalDate.of(10000, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Practice(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun changeOriginalTeachersWithoutChangingPractice() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val teachers = mutableListOf("teacher", "teacher2")
        val session = Session.Practice(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = teachers,
        )

        teachers.clear()

        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun changeReturnedTeachersWithoutChangingPractice() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Practice(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        val returnedTeachers = session.teachers as? MutableList<String>
        if (returnedTeachers != null) {
            try {
                returnedTeachers.clear()
            } catch (_: UnsupportedOperationException) {
            }
        }

        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createExamFailInsufficientTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val incorrectTeachers = listOf("teacher")
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = incorrectTeachers,
            )
        }
    }

    @Test
    fun createExamFailTwinTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val incorrectTeachers = listOf("teacher", "teacher")
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = incorrectTeachers,
            )
        }
    }

    @Test
    fun createExamSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher2", "teacher")
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createExamSuccessDifferentTeacherCase() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("Ivanov", "ivanov")
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
        assertEquals("place", session.place)
        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun createExamSuccessMinimumYear() {
        val expectedDate = LocalDate.of(1, 1, 1)
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = listOf("teacher", "teacher2"),
        )

        assertEquals(1, session.date.year)
    }

    @Test
    fun createExamSuccessMaximumYear() {
        val expectedDate = LocalDate.of(9999, 12, 31)
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = listOf("teacher", "teacher2"),
        )

        assertEquals(9999, session.date.year)
    }

    @Test
    fun createExamFailZeroYear() {
        val incorrectDate = LocalDate.of(0, 1, 1)
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = incorrectDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createExamFailWithoutTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }
    }

    @Test
    fun createExamFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = expectedDate,
                topic = "",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun createExamFailIncorrectDate() {
        val expectedDate = LocalDate.of(10000, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }
    }

    @Test
    fun changeOriginalTeachersWithoutChangingExam() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val teachers = mutableListOf("teacher", "teacher2")
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = teachers,
        )

        teachers.clear()

        assertEquals(expectedTeachers, session.teachers)
    }

    @Test
    fun changeReturnedTeachersWithoutChangingExam() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val session = Session.Exam(
            date = expectedDate,
            topic = "topic",
            place = "place",
            teachersRaw = expectedTeachers,
        )

        val returnedTeachers = session.teachers as? MutableList<String>
        if (returnedTeachers != null) {
            try {
                returnedTeachers.clear()
            } catch (_: UnsupportedOperationException) {
            }
        }

        assertEquals(expectedTeachers, session.teachers)
    }    @Test
    fun createCancelSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Cancel(
            date = expectedDate,
            topic = "topic",
        )

        assertEquals(expectedDate, session.date)
        assertEquals("topic", session.topic)
    }

    @Test
    fun createCancelSuccessMinimumYear() {
        val expectedDate = LocalDate.of(1, 1, 1)
        val session = Session.Cancel(date = expectedDate, topic = "topic")

        assertEquals(1, session.date.year)
    }

    @Test
    fun createCancelSuccessMaximumYear() {
        val expectedDate = LocalDate.of(9999, 12, 31)
        val session = Session.Cancel(date = expectedDate, topic = "topic")

        assertEquals(9999, session.date.year)
    }

    @Test
    fun createCancelFailZeroYear() {
        val incorrectDate = LocalDate.of(0, 1, 1)
        assertFailsWith<InvalidArgumentsException> {
            Session.Cancel(date = incorrectDate, topic = "topic")
        }
    }

    @Test
    fun createCancelFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Cancel(
                date = expectedDate,
                topic = "",
            )
        }
    }

    @Test
    fun createCancelFailIncorrectDate() {
        val expectedDate = LocalDate.of(10000, 10, 13)
        assertFailsWith<InvalidArgumentsException> {
            Session.Cancel(
                date = expectedDate,
                topic = "topic",
            )
        }
    }


}
