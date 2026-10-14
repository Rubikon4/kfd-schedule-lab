package schedule

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals

class TypeCommandTest {
    @Test
    fun runTypeSelectEachTypeSuccess(@TempDir directory: Path) {
        val file = directory.resolve("four-types.txt")
        Files.writeString(
            file,
            "2026-10-05|LECTURE|Topic L|Room 1|Ivanov\r\n" +
                "2026-10-05|PRACTICE|Topic P|Room 2|Petrov\r\n" +
                "2026-10-05|EXAM|Topic E|Room 3|Ivanov,Petrov\r\n" +
                "2026-10-05|CANCELLED|Topic C|-|-\r\n",
        )
        val cases = listOf(
            SessionType.LECTURE to "2026-10-05 | LECTURE | Topic L | Room 1 | Ivanov\nTotal: 1\n",
            SessionType.PRACTICE to "2026-10-05 | PRACTICE | Topic P | Room 2 | Petrov\nTotal: 1\n",
            SessionType.EXAM to "2026-10-05 | EXAM | Topic E | Room 3 | Ivanov, Petrov\nTotal: 1\n",
            SessionType.CANCELLED to "2026-10-05 | CANCELLED | Topic C | - | -\nTotal: 1\n",
        )

        for ((type, expectedOutput) in cases) {
            val (code, output) = captureOutput { runType(Command.ByType(file.toString(), type)) }

            assertEquals(0, code, type.name)
            assertEquals(expectedOutput, output, type.name)
        }
    }

    @Test
    fun runTypeKeepSessionOrderDuplicatesAndTeacherOrder(@TempDir directory: Path) {
        val file = directory.resolve("order.txt")
        Files.writeString(
            file,
            "2026-10-15|EXAM|Later|Room 1|Petrov,Ivanov\n" +
                "2026-10-01|LECTURE|Other|Room 2|Ivanov\n" +
                "2026-10-05|EXAM|Earlier|Room 3|ivanov,Ivanov\n" +
                "2026-10-15|EXAM|Later|Room 1|Petrov,Ivanov\n",
        )

        val (code, output) = captureOutput { runType(Command.ByType(file.toString(), SessionType.EXAM)) }

        assertEquals(0, code)
        assertEquals(
            "2026-10-15 | EXAM | Later | Room 1 | Petrov, Ivanov\n" +
                "2026-10-05 | EXAM | Earlier | Room 3 | ivanov, Ivanov\n" +
                "2026-10-15 | EXAM | Later | Room 1 | Petrov, Ivanov\n" +
                "Total: 3\n",
            output,
        )
    }

    @Test
    fun runTypeNoMatchingSessionsSuccess(@TempDir directory: Path) {
        val file = directory.resolve("other-type.txt")
        Files.writeString(file, "2026-10-05|LECTURE|Topic|Room 1|Ivanov\n")

        val (code, output) = captureOutput { runType(Command.ByType(file.toString(), SessionType.EXAM)) }

        assertEquals(0, code)
        assertEquals("Total: 0\n", output)
    }

    @Test
    fun runTypeEmptyFileSuccessForEachType(@TempDir directory: Path) {
        val file = directory.resolve("empty.txt")
        Files.writeString(file, "")

        for (type in SessionType.entries) {
            val (code, output) = captureOutput { runType(Command.ByType(file.toString(), type)) }

            assertEquals(0, code, type.name)
            assertEquals("Total: 0\n", output, type.name)
        }
    }

    @Test
    fun runTypeRejectOtherTypesAndContinue(@TempDir directory: Path) {
        val file = directory.resolve("mixed.txt")
        Files.writeString(
            file,
            "2026-10-05|LECTURE|Bad before|-|Ivanov\n" +
                "2026-10-15|EXAM|Topic|Room 1|Ivanov,Petrov\n" +
                "2026-10-05|PRACTICE|Bad after|Room 2|Petrov,Petrov\n",
        )

        val (code, output) = captureOutput { runType(Command.ByType(file.toString(), SessionType.EXAM)) }

        assertEquals(1, code)
        assertEquals("2026-10-15 | EXAM | Topic | Room 1 | Ivanov, Petrov\nTotal: 1\n", output)
    }

    @Test
    fun runTypeRejectedFileWithoutMatches(@TempDir directory: Path) {
        val file = directory.resolve("rejected.txt")
        Files.writeString(file, "2026-10-05|LECTURE|Topic|-|Ivanov\n")

        val (code, output) = captureOutput { runType(Command.ByType(file.toString(), SessionType.EXAM)) }

        assertEquals(1, code)
        assertEquals("Total: 0\n", output)
    }

    @Test
    fun runTypeSamplesLikeExpectedFiles() {
        val root = Path.of(System.getProperty("repo.root"))
        for ((name, expectedCode) in listOf("clean" to 0, "dirty" to 1, "empty" to 0)) {
            val file = root.resolve("samples/$name.txt")
            val expectedOutput = Files.readString(root.resolve("samples/$name.type.expected")).replace("\r\n", "\n")

            val (code, output) = captureOutput {
                runApplication(arrayOf("type", "--file", file.toString(), "--type", "EXAM"))
            }

            assertEquals(expectedCode, code, name)
            assertEquals(expectedOutput, output, name)
        }
    }

    @Test
    fun runTypeReadingFailureHandledByMain(@TempDir directory: Path) {
        for (file in listOf(directory.resolve("missing.txt"), directory)) {
            val (code, output) = captureOutput {
                runApplication(arrayOf("type", "--file", file.toString(), "--type", "EXAM"))
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
