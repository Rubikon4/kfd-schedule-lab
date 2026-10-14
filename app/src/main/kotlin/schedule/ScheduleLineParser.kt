package schedule

import java.time.LocalDate
import java.time.format.DateTimeParseException

class ScheduleLineParser {
    private val fieldNames = listOf("date", "type", "topic", "location", "teachers")
    private val datePattern = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")

    fun scheduleParseLine(currentLineNumber: Long, line: String): LineParseResult {
        if (!line.contains('|')) {
            return IncorrectLine(lineNumber = currentLineNumber, invalidReason = InvalidFieldSeparator)
        }

        val fields = line.split('|')
        if (fields.size != 5) {
            return IncorrectLine(currentLineNumber, InvalidFieldCount(fields.size))
        }

        val topic = fields[2]
        val location = fields[3]
        val teachers = fields[4].split(',')

        for ((index, value) in fields.withIndex()) {
            if (value.isBlank()) {
                return IncorrectLine(currentLineNumber, EmptyRequiredField(fieldNames[index]))
            }
        }

        val dateValue = fields[0]
        if (!datePattern.matches(dateValue)) {
            return IncorrectLine(currentLineNumber, InvalidDate(dateValue))
        }
        val date = try {
            LocalDate.parse(dateValue)
        } catch (_: DateTimeParseException) {
            return IncorrectLine(currentLineNumber, InvalidDate(dateValue))
        }
        if (date.year !in 1..9999) {
            return IncorrectLine(currentLineNumber, InvalidDate(dateValue))
        }

        val type = SessionType.entries.firstOrNull { it.name == fields[1] }
            ?: return IncorrectLine(currentLineNumber, UnknownSessionType(fields[1]))


        if (type == SessionType.EXAM) {
            val count = teachers.count { it.isNotBlank() && it != "-" }
            if (count < 2) {
                return IncorrectLine(currentLineNumber, InsufficientExamTeachers(count))
            }
        }

        for ((index, value) in fields.withIndex()) {
            val values = if (index == 4) teachers else listOf(value)
            for (element in values) {
                if (element.any { Character.isISOControl(it.code) }) {
                    return IncorrectLine(currentLineNumber, ControlCharacters(fieldNames[index], element))
                }
            }
        }

        for ((index, value) in fields.withIndex()) {
            val values = if (index == 4) teachers else listOf(value)
            for (element in values) {
                if (element != element.trim()) {
                    return IncorrectLine(currentLineNumber, OuterWhitespace(fieldNames[index], element))
                }
            }
        }

        if (type == SessionType.CANCELLED) {
            for (index in 3..4) {
                if (fields[index] != "-") {
                    return IncorrectLine(currentLineNumber, InvalidCancelledField(fieldNames[index], fields[index]))
                }
            }
        } else {
            if (location == "-") {
                return IncorrectLine(currentLineNumber, InvalidLocation(location))
            }

            for ((index, name) in teachers.withIndex()) {
                if (name.isBlank() || name == "-") {
                    return IncorrectLine(currentLineNumber, InvalidTeacher(index + 1, name))
                }
            }

            val seenTeachers = mutableSetOf<String>()
            for ((index, name) in teachers.withIndex()) {
                if (!seenTeachers.add(name)) {
                    return IncorrectLine(currentLineNumber, DuplicateTeacher(index + 1, name))
                }
            }
        }

        val session = when (type) {
            SessionType.LECTURE -> Session.Lecture(date, topic, location, teachers)
            SessionType.PRACTICE -> Session.Practice(date, topic, location, teachers)
            SessionType.EXAM -> Session.Exam(date, topic, location, teachers)
            SessionType.CANCELLED -> Session.Canceled(date, topic)
        }
        return CorrectLine(session)
    }
}
