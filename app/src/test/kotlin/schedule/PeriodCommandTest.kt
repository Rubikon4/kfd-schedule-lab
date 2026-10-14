package schedule

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.assertEquals

class PeriodCommandTest {
    @Test
    fun runPeriodInclusiveDatesAllTypesKeepOrderAndDuplicates(@TempDir directory: Path) {
        val file = directory.resolve("period.txt")
        Files.writeString(
            file,
            "2026-09-30|LECTURE|Before|Room 1|Ivanov\n" +
                "2026-10-31|EXAM|End|Room 2|Ivanov,Petrov\n" +
                "2026-10-01|LECTURE|Start|Room 1|Ivanov\n" +
                "2026-10-15|PRACTICE|Middle|Room 3|Petrov\n" +
                "2026-10-20|CANCELLED|Cancelled|-|-\n" +
                "2026-10-01|LECTURE|Start|Room 1|Ivanov\n" +
                "2026-11-01|LECTURE|After|Room 1|Ivanov\n",
        )
        val command = Command.Period(file.toString(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))

        val (code, output) = captureOutput { runPeriod(command) }

        assertEquals(0, code)
        assertEquals(
            "2026-10-31 | EXAM | End | Room 2\n" +
                "2026-10-01 | LECTURE | Start | Room 1\n" +
                "2026-10-15 | PRACTICE | Middle | Room 3\n" +
                "2026-10-20 | CANCELLED | Cancelled | -\n" +
                "2026-10-01 | LECTURE | Start | Room 1\n" +
                "Total: 5\n",
            output,
        )
    }

    @Test
    fun runPeriodSameDateSuccess(@TempDir directory: Path) {
        val file = directory.resolve("same-date.txt")
        Files.writeString(file, "2026-10-05|LECTURE|Topic|Room 1|Ivanov\r\n")
        val expectedDate = LocalDate.of(2026, 10, 5)

        val (code, output) = captureOutput {
            runPeriod(Command.Period(file.toString(), expectedDate, expectedDate))
        }

        assertEquals(0, code)
        assertEquals("2026-10-05 | LECTURE | Topic | Room 1\nTotal: 1\n", output)
    }

    @Test
    fun runPeriodNoMatchingSessionsSuccess(@TempDir directory: Path) {
        val file = directory.resolve("outside.txt")
        Files.writeString(file, "2026-11-05|LECTURE|Topic|Room 1|Ivanov\n")
        val command = Command.Period(file.toString(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))

        val (code, output) = captureOutput { runPeriod(command) }

        assertEquals(0, code)
        assertEquals("Total: 0\n", output)
    }

    @Test
    fun runPeriodRejectOutsideRangeAndContinue(@TempDir directory: Path) {
        val file = directory.resolve("mixed.txt")
        Files.writeString(
            file,
            "2026-09-05|LECTURE|Bad before|-|Ivanov\n" +
                "2026-10-05|LECTURE|Topic|Room 1|Ivanov\n" +
                "2026-11-05|LECTURE|Bad after|Room 1|Ivanov,Ivanov\n",
        )
        val command = Command.Period(file.toString(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))

        val (code, output) = captureOutput { runPeriod(command) }

        assertEquals(1, code)
        assertEquals("2026-10-05 | LECTURE | Topic | Room 1\nTotal: 1\n", output)
    }

    @Test
    fun runPeriodRejectedFileWithoutMatches(@TempDir directory: Path) {
        val file = directory.resolve("rejected.txt")
        Files.writeString(file, "2026-11-05|LECTURE|Topic|-|Ivanov\n")
        val command = Command.Period(file.toString(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))

        val (code, output) = captureOutput { runPeriod(command) }

        assertEquals(1, code)
        assertEquals("Total: 0\n", output)
    }

    @Test
    fun runPeriodSamplesLikeExpectedFiles() {
        val root = Path.of(System.getProperty("repo.root"))
        for ((name, expectedCode) in listOf("clean" to 0, "dirty" to 1, "empty" to 0)) {
            val file = root.resolve("samples/$name.txt")
            val expectedOutput = Files.readString(root.resolve("samples/$name.period.expected")).replace("\r\n", "\n")

            val (code, output) = captureOutput {
                runApplication(arrayOf("period", "--file", file.toString(), "--from", "2026-09-28", "--to", "2027-05-13"))
            }

            assertEquals(expectedCode, code, name)
            assertEquals(expectedOutput, output, name)
        }
    }

    @Test
    fun runPeriodOctoberLikeExpectedFile() {
        val root = Path.of(System.getProperty("repo.root"))
        val command = Command.Period(root.resolve("samples/dirty.txt").toString(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))
        val expectedOutput = Files.readString(root.resolve("samples/dirty.october.expected")).replace("\r\n", "\n")

        val (code, output) = captureOutput { runPeriod(command) }

        assertEquals(1, code)
        assertEquals(expectedOutput, output)
    }

    @Test
    fun runPeriodReadingFailureHandledByMain(@TempDir directory: Path) {
        for (file in listOf(directory.resolve("missing.txt"), directory)) {
            val (code, output) = captureOutput {
                runApplication(arrayOf("period", "--file", file.toString(), "--from", "2026-10-01", "--to", "2026-10-31"))
            }

            assertEquals(2, code)
            assertEquals("Cannot read file\n", output)
        }
    }

    private fun captureOutput(action: () -> Int): Pair<Int, String> {
        val originalOutput = System.out
        val buffer = ByteArrayOutputStream()
        val output = PrintStream(buffer, true, StandardCharsets.UTF_8)
        try {
            System.setOut(output)
            val code = action()
            return code to buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n")
        } finally {
            System.setOut(originalOutput)
            output.close()
        }
    }
}
