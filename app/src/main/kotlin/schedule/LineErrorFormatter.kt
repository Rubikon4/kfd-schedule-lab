package schedule

fun formatLineError(error: IncorrectLine): String {
    val message = when (val reason = error.invalidReason) {
        InvalidFieldSeparator -> "invalid field separator"
        is InvalidFieldCount -> "got ${reason.actualCount} fields, expected 5"
        is EmptyRequiredField -> "empty required field ${quoteErrorValue(reason.fieldName)}"
        is InvalidDate -> "invalid date ${quoteErrorValue(reason.value)}"
        is UnknownSessionType -> "unknown session type ${quoteErrorValue(reason.value)}"
        is InsufficientExamTeachers -> "type \"EXAM\" requires at least 2 teachers, got ${reason.actualCount}"
        is ControlCharacters ->
            "control characters in field ${quoteErrorValue(reason.fieldName)}: ${quoteErrorValue(reason.value)}"
        is OuterWhitespace ->
            "leading or trailing whitespace in field ${quoteErrorValue(reason.fieldName)}: ${quoteErrorValue(reason.value)}"
        is InvalidCancelledField ->
            "type \"CANCELLED\" requires field ${quoteErrorValue(reason.fieldName)} to be \"-\", got ${quoteErrorValue(reason.value)}"
        is InvalidLocation -> "invalid location ${quoteErrorValue(reason.value)}"
        is InvalidTeacher -> "invalid teacher ${reason.teacherNumber} ${quoteErrorValue(reason.value)}"
        is DuplicateTeacher -> "duplicate teacher ${reason.teacherNumber} ${quoteErrorValue(reason.value)}"
    }
    return "line ${error.lineNumber}: $message"
}

fun escapeErrorValue(value: String): String = buildString {
    for (symbol in value) {
        when (symbol) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            else -> {
                if (Character.isISOControl(symbol.code) || symbol.code > 0x7F) {
                    append("\\u")
                    append(symbol.code.toString(16).uppercase().padStart(4, '0'))
                } else {
                    append(symbol)
                }
            }
        }
    }
}

private fun quoteErrorValue(value: String): String = "\"${escapeErrorValue(value)}\""
