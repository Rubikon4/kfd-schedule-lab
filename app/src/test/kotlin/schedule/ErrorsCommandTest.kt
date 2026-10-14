package schedule

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ErrorsCommandTest {
    @Test
    fun runErrorsCorrectFileSuccess(@TempDir directory: Path) {
        val file = directory.resolve("correct.txt")
        Files.writeString(file, "2026-10-05|LECTURE|Topic|Room 1|Ivanov\r\n2026-10-06|CANCELLED|Topic|-|-\r\n")

        val (code, output) = captureOutput { runErrors(Command.Errors(file.toString())) }

        assertEquals(0, code)
        assertEquals("Rejected: 0 of 2\n", output)
    }

    @Test
    fun runErrorsEmptyFileSuccess(@TempDir directory: Path) {
        val file = directory.resolve("empty.txt")
        Files.writeString(file, "")

        val (code, output) = captureOutput { runErrors(Command.Errors(file.toString())) }

        assertEquals(0, code)
        assertEquals("Rejected: 0 of 0\n", output)
    }

    @Test
    fun runErrorsBlankLineRejected(@TempDir directory: Path) {
        val file = directory.resolve("blank.txt")
        Files.writeString(file, "\n")

        val (code, output) = captureOutput { runErrors(Command.Errors(file.toString())) }

        assertEquals(1, code)
        assertEquals("line 1: invalid field separator\nRejected: 1 of 1\n", output)
    }

    @Test
    fun runErrorsMixedFileKeepOrderAndContinue(@TempDir directory: Path) {
        val file = directory.resolve("mixed.txt")
        Files.writeString(
            file,
            "2026-13-45|LECTURE|Topic|Room 1|Ivanov\n" +
                "2026-10-05|LECTURE|Topic|Room 1|Ivanov\n" +
                "2026-10-05|SEMINAR|Topic|Room 1|Ivanov\n" +
                "2026-10-06|CANCELLED|Topic|-|-",
        )

        val (code, output) = captureOutput { runErrors(Command.Errors(file.toString())) }

        assertEquals(1, code)
        assertEquals(
            "line 1: invalid date \"2026-13-45\"\n" +
                "line 3: unknown session type \"SEMINAR\"\n" +
                "Rejected: 2 of 4\n",
            output,
        )
    }

    @Test
    fun runErrorsSamplesLikeExpectedFiles() {
        val root = Path.of(System.getProperty("repo.root"))
        for ((name, expectedCode) in listOf("clean" to 0, "dirty" to 1, "empty" to 0)) {
            val file = root.resolve("samples/$name.txt")
            val expectedOutput = Files.readString(root.resolve("samples/$name.errors.expected")).replace("\r\n", "\n")

            val (code, output) = captureOutput {
                runApplication(arrayOf("errors", "--file", file.toString()))
            }

            assertEquals(expectedCode, code, name)
            assertEquals(expectedOutput, output, name)
        }
    }

    @Test
    fun runErrorsReadingFailureHandledByMain(@TempDir directory: Path) {
        val missingFile = directory.resolve("missing.txt")

        assertFailsWith<IOException> {
            runErrors(Command.Errors(missingFile.toString()))
        }

        for (file in listOf(missingFile, directory)) {
            val (code, output) = captureOutput {
                runApplication(arrayOf("errors", "--file", file.toString()))
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
