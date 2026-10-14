package schedule

import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScheduleLineParserTest {
    private val parser = ScheduleLineParser()

    @Test
    fun parseLectureSuccess() {
        val result = parser.scheduleParseLine(1, "2026-10-05|LECTURE|Kotlin basics|Room 312|Ivan Petrov")
        val session = assertIs<Session.Lecture>(assertIs<CorrectLine>(result).session)

        assertEquals(LocalDate.of(2026, 10, 5), session.date)
        assertEquals("Kotlin basics", session.topic)
        assertEquals("Room 312", session.place)
        assertEquals(listOf("Ivan Petrov"), session.teachers)
    }

    @Test
    fun parsePracticeSuccess() {
        val result = parser.scheduleParseLine(1, "2026-10-05|PRACTICE|Topic|Room 1|Petrov,Ivanov")
        val session = assertIs<Session.Practice>(assertIs<CorrectLine>(result).session)

        assertEquals(LocalDate.of(2026, 10, 5), session.date)
        assertEquals("Topic", session.topic)
        assertEquals("Room 1", session.place)
        assertEquals(listOf("Petrov", "Ivanov"), session.teachers)
    }

    @Test
    fun parseExamSuccessKeepOrderAndCase() {
        val result = parser.scheduleParseLine(1, "2026-10-05|EXAM|Topic|Room 1|ivanov,Ivanov")
        val session = assertIs<Session.Exam>(assertIs<CorrectLine>(result).session)

        assertEquals(LocalDate.of(2026, 10, 5), session.date)
        assertEquals("Topic", session.topic)
        assertEquals("Room 1", session.place)
        assertEquals(listOf("ivanov", "Ivanov"), session.teachers)
    }

    @Test
    fun parseCancelledSuccess() {
        val result = parser.scheduleParseLine(1, "2026-10-05|CANCELLED|Topic|-|-")
        val session = assertIs<Session.Canceled>(assertIs<CorrectLine>(result).session)

        assertEquals(LocalDate.of(2026, 10, 5), session.date)
        assertEquals("Topic", session.topic)
    }

    @Test
    fun parseDateBoundariesAndLeapDaySuccess() {
        for (dateValue in listOf("0001-01-01", "9999-12-31", "2024-02-29")) {
            val result = parser.scheduleParseLine(1, "$dateValue|LECTURE|Topic|Room 1|Ivanov")
            val session = assertIs<CorrectLine>(result).session

            assertEquals(LocalDate.parse(dateValue), session.date)
        }
    }

    @Test
    fun parseInvalidDate() {
        for (dateValue in listOf("0000-01-01", "10000-01-01", "2026-02-29", "2026-13-45", "2026-1-01", " 2026-10-05", "+2026-10-05")) {
            val result = parser.scheduleParseLine(7, "$dateValue|LECTURE|Topic|Room 1|Ivanov")

            assertEquals(IncorrectLine(7, InvalidDate(dateValue)), result)
        }
    }

    @Test
    fun parseInvalidSeparatorIncludingEmptyLine() {
        for (line in listOf("", "   ", "2026-10-05;LECTURE;Topic;Room 1;Ivanov")) {
            assertEquals(IncorrectLine(7, InvalidFieldSeparator), parser.scheduleParseLine(7, line))
        }
    }

    @Test
    fun parseInvalidFieldCount() {
        assertEquals(
            IncorrectLine(7, InvalidFieldCount(4)),
            parser.scheduleParseLine(7, "2026-10-05|LECTURE|Topic|Room 1"),
        )
        assertEquals(
            IncorrectLine(7, InvalidFieldCount(6)),
            parser.scheduleParseLine(7, "2026-10-05|LECTURE|Topic|Room 1|Ivanov|"),
        )
    }

    @Test
    fun parseEmptyRequiredFieldsIncludingTrailingEmptyFields() {
        val cases = listOf(
            "|LECTURE|Topic|Room 1|Ivanov" to "date",
            "2026-10-05| |Topic|Room 1|Ivanov" to "type",
            "2026-10-05|LECTURE|   |Room 1|Ivanov" to "topic",
            "2026-10-05|LECTURE|Topic||Ivanov" to "location",
            "2026-10-05|LECTURE|Topic|Room 1|" to "teachers",
        )
        for ((line, fieldName) in cases) {
            assertEquals(IncorrectLine(7, EmptyRequiredField(fieldName)), parser.scheduleParseLine(7, line))
        }
    }

    @Test
    fun parseUnknownSessionType() {
        for (typeValue in listOf("SEMINAR", "lecture", " LECTURE")) {
            assertEquals(
                IncorrectLine(7, UnknownSessionType(typeValue)),
                parser.scheduleParseLine(7, "2026-10-05|$typeValue|Topic|Room 1|Ivanov"),
            )
        }
    }

    @Test
    fun parseInsufficientExamTeachers() {
        for ((teachersValue, count) in listOf("Ivanov" to 1, "Ivanov,-,, " to 1, "-,," to 0)) {
            assertEquals(
                IncorrectLine(7, InsufficientExamTeachers(count)),
                parser.scheduleParseLine(7, "2026-10-05|EXAM|Topic|Room 1|$teachersValue"),
            )
        }
    }

    @Test
    fun parseControlCharacters() {
        val cases = listOf(
            "2026-10-05|LECTURE|Top\nic|Room 1|Ivanov" to ControlCharacters("topic", "Top\nic"),
            "2026-10-05|LECTURE|Topic|Room\u0000 1|Ivanov" to ControlCharacters("location", "Room\u0000 1"),
            "2026-10-05|LECTURE|Topic|Room 1|Ivanov,Pet\trov" to ControlCharacters("teachers", "Pet\trov"),
            "2026-10-05|LECTURE|Topic|Room 1|Ivanov\u007f" to ControlCharacters("teachers", "Ivanov\u007f"),
            "2026-10-05|LECTURE|Topic|Room 1|Ivanov\u009f" to ControlCharacters("teachers", "Ivanov\u009f"),
        )
        for ((line, reason) in cases) {
            assertEquals(IncorrectLine(7, reason), parser.scheduleParseLine(7, line))
        }
    }

    @Test
    fun parseOuterWhitespaceIncludingTeacherElements() {
        val cases = listOf(
            "2026-10-05|LECTURE| Topic|Room 1|Ivanov" to OuterWhitespace("topic", " Topic"),
            "2026-10-05|LECTURE|Topic|Room 1 |Ivanov" to OuterWhitespace("location", "Room 1 "),
            "2026-10-05|LECTURE|Topic|Room 1|Ivanov, Petrov" to OuterWhitespace("teachers", " Petrov"),
            "2026-10-05|LECTURE|Topic|Room 1| Ivanov,Petrov " to OuterWhitespace("teachers", " Ivanov"),
        )
        for ((line, reason) in cases) {
            assertEquals(IncorrectLine(7, reason), parser.scheduleParseLine(7, line))
        }
    }

    @Test
    fun parseInvalidCancelledFields() {
        assertEquals(
            IncorrectLine(7, InvalidCancelledField("location", "Room 1")),
            parser.scheduleParseLine(7, "2026-10-05|CANCELLED|Topic|Room 1|Ivanov"),
        )
        assertEquals(
            IncorrectLine(7, InvalidCancelledField("teachers", "Ivanov")),
            parser.scheduleParseLine(7, "2026-10-05|CANCELLED|Topic|-|Ivanov"),
        )
    }

    @Test
    fun parseInvalidLocation() {
        assertEquals(
            IncorrectLine(7, InvalidLocation("-")),
            parser.scheduleParseLine(7, "2026-10-05|LECTURE|Topic|-|Ivanov"),
        )
    }

    @Test
    fun parseInvalidTeacherElementsIncludingTrailingEmptyElement() {
        for ((teachersValue, reason) in listOf(
            ",Ivanov" to InvalidTeacher(1, ""),
            "Ivanov," to InvalidTeacher(2, ""),
            "Ivanov,-" to InvalidTeacher(2, "-"),
            "Ivanov,Petrov,," to InvalidTeacher(3, ""),
        )) {
            assertEquals(
                IncorrectLine(7, reason),
                parser.scheduleParseLine(7, "2026-10-05|LECTURE|Topic|Room 1|$teachersValue"),
            )
        }
    }

    @Test
    fun parseDuplicateTeacherIncludingExam() {
        assertEquals(
            IncorrectLine(7, DuplicateTeacher(2, "Ivanov")),
            parser.scheduleParseLine(7, "2026-10-05|EXAM|Topic|Room 1|Ivanov,Ivanov"),
        )
        assertEquals(
            IncorrectLine(7, DuplicateTeacher(3, "Petrov")),
            parser.scheduleParseLine(7, "2026-10-05|LECTURE|Topic|Room 1|Ivanov,Petrov,Petrov,Ivanov"),
        )
    }

    @Test
    fun parseMultipleDefectsInRequiredOrder() {
        val cases = listOf(
            "|SEMINAR|Topic|Room 1|Ivanov|extra" to InvalidFieldCount(6),
            "2026-13-45|SEMINAR||Room 1|Ivanov" to EmptyRequiredField("topic"),
            "||Topic|Room 1|Ivanov" to EmptyRequiredField("date"),
            "2026-13-45|SEMINAR|Topic|Room 1|Ivanov" to InvalidDate("2026-13-45"),
            "2026-10-05|SEMINAR|Top\tic|Room 1|Ivanov" to UnknownSessionType("SEMINAR"),
            "2026-10-05|EXAM|Top\tic|Room 1|Ivanov" to InsufficientExamTeachers(1),
            "2026-10-05|LECTURE| Topic|Room\t1|Ivanov" to ControlCharacters("location", "Room\t1"),
            "2026-10-05|LECTURE|Top\tic|Room\t1|Ivanov" to ControlCharacters("topic", "Top\tic"),
            "2026-10-05|CANCELLED| Topic|Room 1|Ivanov" to OuterWhitespace("topic", " Topic"),
            "2026-10-05|LECTURE|Topic|-|-" to InvalidLocation("-"),
            "2026-10-05|EXAM|Topic|Room 1|Ivanov,Ivanov,-" to InvalidTeacher(3, "-"),
        )
        for ((line, reason) in cases) {
            assertEquals(IncorrectLine(7, reason), parser.scheduleParseLine(7, line), line)
        }
    }

    @Test
    fun parseCorrectLineAfterRejectedLine() {
        assertIs<IncorrectLine>(parser.scheduleParseLine(1, "broken"))
        assertIs<CorrectLine>(parser.scheduleParseLine(2, "2026-10-05|LECTURE|Topic|Room 1|Ivanov"))
    }

    @Test
    fun parseSamplesWithExpectedCounts() {
        val root = Path.of(System.getProperty("repo.root"))
        val cases = listOf(
            Triple("clean.txt", 200, 0),
            Triple("dirty.txt", 500, 164),
            Triple("empty.txt", 0, 0),
        )
        for ((fileName, expectedLines, expectedRejected) in cases) {
            var lines = 0
            var rejected = 0
            forEachScheduleLine(root.resolve("samples").resolve(fileName).toString()) { number, line ->
                lines++
                if (parser.scheduleParseLine(number, line) is IncorrectLine) rejected++
            }

            assertEquals(expectedLines, lines, fileName)
            assertEquals(expectedRejected, rejected, fileName)
        }
    }
}
