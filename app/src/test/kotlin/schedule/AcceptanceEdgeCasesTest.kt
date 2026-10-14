package schedule

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AcceptanceEdgeCasesTest {
    @Test
    fun runCommandsCustomBoundariesLikeExpectedFiles() {
        val root = Path.of(System.getProperty("repo.root"))
        val file = root.resolve("samples/custom-boundaries.txt").toString()
        val cases = listOf(
            "period" to arrayOf("period", "--file", file, "--from", "2026-10-01", "--to", "2026-10-31"),
            "type" to arrayOf("type", "--file", file, "--type", "EXAM"),
            "errors" to arrayOf("errors", "--file", file),
        )

        for ((name, args) in cases) {
            val expectedOutput = Files.readString(root.resolve("samples/custom-boundaries.$name.expected")).replace("\r\n", "\n")
            val (code, output) = captureOutput { runApplication(args) }

            assertEquals(1, code, name)
            assertEquals(expectedOutput, output, name)
        }
    }

    @Test
    fun readingFailureAfterOutputKeepsRowsWithoutSummary(@TempDir directory: Path) {
        val file = directory.resolve("damaged-utf8.txt")
        val prefix = ("2026-10-05|LECTURE|Topic|Room 1|Ivanov\n" + "broken\n").repeat(2000)
        Files.write(file, prefix.toByteArray(StandardCharsets.UTF_8) + byteArrayOf(0xC3.toByte(), 0x28))
        val cases = listOf(
            arrayOf("period", "--file", file.toString(), "--from", "2026-10-01", "--to", "2026-10-31") to
                "2026-10-05 | LECTURE | Topic | Room 1\n",
            arrayOf("type", "--file", file.toString(), "--type", "LECTURE") to
                "2026-10-05 | LECTURE | Topic | Room 1 | Ivanov\n",
            arrayOf("errors", "--file", file.toString()) to "line 2: invalid field separator\n",
        )

        for ((args, expectedFirstLine) in cases) {
            val (code, output) = captureOutput { runApplication(args) }

            assertEquals(2, code, args[0])
            assertTrue(output.startsWith(expectedFirstLine), args[0])
            assertTrue(output.endsWith("Cannot read file\n"), args[0])
            assertFalse(output.contains("Total:") || output.contains("Rejected:"), args[0])
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
