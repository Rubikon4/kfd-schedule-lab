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
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "",
                place = "place",
                teachersRaw = expectedTeachers,
            )
        }

        assertEquals("topic must not be blank", exception.message)
    }

    @Test
    fun createLectureFailIncorrectDate() {
        val incorrectDate = LocalDate.of(10000, 10, 13)
        val expectedTeachers = listOf("teacher", "teacher2")
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = incorrectDate,
                topic = "topic",
                place = "place",
                teachersRaw = expectedTeachers,
            )
        }

        assertEquals("date year must be between 1 and 9999", exception.message)
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
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("date year must be between 1 and 9999", exception.message)
    }

    @Test
    fun createLectureFailWithoutTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }

        assertEquals("LECTURE requires at least 1 teacher", exception.message)
    }

    @Test
    fun createLectureFailBlankTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "   ",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("topic must not be blank", exception.message)
    }

    @Test
    fun createLectureFailOuterSpaceTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = " topic ",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("topic must not have leading or trailing whitespace", exception.message)
    }

    @Test
    fun createLectureFailControlCharacterTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic\tname",
                place = "place",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("topic must not contain control characters", exception.message)
    }

    @Test
    fun createLectureFailWithoutPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("place must not be blank", exception.message)
    }

    @Test
    fun createLectureFailBlankPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "   ",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("place must not be blank", exception.message)
    }

    @Test
    fun createLectureFailDashPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "-",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("place must not be '-'", exception.message)
    }

    @Test
    fun createLectureFailOuterSpacePlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place ",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("place must not have leading or trailing whitespace", exception.message)
    }

    @Test
    fun createLectureFailControlCharacterPlace() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "Room\u0000 312",
                teachersRaw = listOf("teacher", "teacher2"),
            )
        }

        assertEquals("place must not contain control characters", exception.message)
    }

    @Test
    fun createLectureFailEmptyTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", ""),
            )
        }

        assertEquals("teacher 2 must not be blank", exception.message)
    }

    @Test
    fun createLectureFailBlankTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "   "),
            )
        }

        assertEquals("teacher 2 must not be blank", exception.message)
    }

    @Test
    fun createLectureFailDashTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "-"),
            )
        }

        assertEquals("teacher 2 must not be '-'", exception.message)
    }

    @Test
    fun createLectureFailOuterSpaceTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", " teacher2"),
            )
        }

        assertEquals("teacher 2 must not have leading or trailing whitespace", exception.message)
    }

    @Test
    fun createLectureFailControlCharacterTeacher() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher\nname"),
            )
        }

        assertEquals("teacher 2 must not contain control characters", exception.message)
    }

    @Test
    fun createLectureFailTwinTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Lecture(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = listOf("teacher", "teacher"),
            )
        }

        assertEquals("teacher names must be unique", exception.message)
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
        assertFailsWith<IllegalArgumentException> {
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
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Practice(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }

        assertEquals("PRACTICE requires at least 1 teacher", exception.message)
    }

    @Test
    fun createPracticeFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<IllegalArgumentException> {
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
        assertFailsWith<IllegalArgumentException> {
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
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = incorrectTeachers,
            )
        }

        assertEquals("EXAM requires at least 2 teachers", exception.message)
    }

    @Test
    fun createExamFailTwinTeachers() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val incorrectTeachers = listOf("teacher", "teacher")
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = incorrectTeachers,
            )
        }

        assertEquals("teacher names must be unique", exception.message)
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
        assertFailsWith<IllegalArgumentException> {
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
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Exam(
                date = expectedDate,
                topic = "topic",
                place = "place",
                teachersRaw = emptyList(),
            )
        }

        assertEquals("EXAM requires at least 2 teachers", exception.message)
    }

    @Test
    fun createExamFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        assertFailsWith<IllegalArgumentException> {
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
        assertFailsWith<IllegalArgumentException> {
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
        assertFailsWith<IllegalArgumentException> {
            Session.Cancel(date = incorrectDate, topic = "topic")
        }
    }

    @Test
    fun createCancelFailWithoutTopic() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val exception = assertFailsWith<IllegalArgumentException> {
            Session.Cancel(
                date = expectedDate,
                topic = "",
            )
        }

        assertEquals("topic must not be blank", exception.message)
    }

    @Test
    fun createCancelFailIncorrectDate() {
        val expectedDate = LocalDate.of(10000, 10, 13)
        assertFailsWith<IllegalArgumentException> {
            Session.Cancel(
                date = expectedDate,
                topic = "topic",
            )
        }
    }


}
