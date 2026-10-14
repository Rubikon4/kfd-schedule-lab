package schedule

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LineErrorFormatterTest {
    @Test
    fun formatRequiredMessages() {
        val cases = listOf(
            InvalidDate("2026-13-45") to "invalid date \"2026-13-45\"",
            EmptyRequiredField("date") to "empty required field \"date\"",
            InvalidFieldSeparator to "invalid field separator",
            UnknownSessionType("SEMINAR") to "unknown session type \"SEMINAR\"",
            InsufficientExamTeachers(1) to "type \"EXAM\" requires at least 2 teachers, got 1",
            InvalidFieldCount(6) to "got 6 fields, expected 5",
        )

        for ((reason, expectedMessage) in cases) {
            assertEquals("line 7: $expectedMessage", formatLineError(IncorrectLine(7, reason)))
        }
    }

    @Test
    fun formatOtherReasons() {
        val cases = listOf(
            ControlCharacters("topic", "Top\tic") to "control characters in field \"topic\": \"Top\\u0009ic\"",
            OuterWhitespace("teachers", " Ivanov") to "leading or trailing whitespace in field \"teachers\": \" Ivanov\"",
            InvalidCancelledField("location", "Room 1") to "type \"CANCELLED\" requires field \"location\" to be \"-\", got \"Room 1\"",
            InvalidLocation("-") to "invalid location \"-\"",
            InvalidTeacher(2, "") to "invalid teacher 2 \"\"",
            DuplicateTeacher(3, "Ivanov") to "duplicate teacher 3 \"Ivanov\"",
        )

        for ((reason, expectedMessage) in cases) {
            assertEquals("line 7: $expectedMessage", formatLineError(IncorrectLine(7, reason)))
        }
    }

    @Test
    fun escapeQuotesAndBackslashes() {
        assertEquals("quote\\\" and path\\\\name", escapeErrorValue("quote\" and path\\name"))
    }

    @Test
    fun escapeControlCharacters() {
        val value = "\u0000\t\n\r\u001F\u007F\u0085"
        val expectedValue = "\\u0000\\u0009\\u000A\\u000D\\u001F\\u007F\\u0085"

        assertEquals(expectedValue, escapeErrorValue(value))
    }

    @Test
    fun escapeCyrillicAndNonAsciiWhitespace() {
        assertEquals("\\u0418\\u0432\\u0430\\u043D\\u043E\\u0432", escapeErrorValue("Иванов"))
        assertEquals("\\u00A0\\u2028", escapeErrorValue("\u00A0\u2028"))
    }

    @Test
    fun escapeEachUtf16SymbolOfEmoji() {
        assertEquals("\\uD83D\\uDE00", escapeErrorValue("\uD83D\uDE00"))
    }

    @Test
    fun keepPrintableAsciiUnchanged() {
        assertEquals("Room 312 - Ivanov, Petrov", escapeErrorValue("Room 312 - Ivanov, Petrov"))
        assertEquals("", escapeErrorValue(""))
    }

    @Test
    fun formatDamagedValuesWithoutAddingLinesOrNonAscii() {
        val value = "quote\"\\path\nЖ"
        val reason = InvalidDate(value)
        val result = formatLineError(IncorrectLine(7, reason))

        assertEquals("line 7: invalid date \"quote\\\"\\\\path\\u000A\\u0416\"", result)
        assertTrue(result.all { it.code in 0x20..0x7E })
        assertEquals(value, reason.value)
    }

    @Test
    fun escapeFieldNamesAsWellAsValues() {
        val result = formatLineError(IncorrectLine(7, EmptyRequiredField("to\npик")))

        assertEquals("line 7: empty required field \"to\\u000Ap\\u0438\\u043A\"", result)
    }

    @Test
    fun formatLongLineNumber() {
        assertEquals(
            "line 3000000000: invalid field separator",
            formatLineError(IncorrectLine(3_000_000_000L, InvalidFieldSeparator)),
        )
    }

    @Test
    fun formatDirtySampleErrorsLikeExpectedFile() {
        val root = Path.of(System.getProperty("repo.root"))
        val parser = ScheduleLineParser()
        val actualMessages = mutableListOf<String>()
        forEachScheduleLine(root.resolve("samples/dirty.txt").toString()) { number, line ->
            val result = parser.scheduleParseLine(number, line)
            if (result is IncorrectLine) actualMessages.add(formatLineError(result))
        }
        val expectedMessages = Files.readAllLines(root.resolve("samples/dirty.errors.expected")).dropLast(1)

        assertEquals(expectedMessages, actualMessages)
    }
}
