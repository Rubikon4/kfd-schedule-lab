package schedule

sealed interface LineParseResult {}

data class CorrectLine(val session: Session) : LineParseResult {}
data class IncorrectLine(val lineNumber: String, val invalidReason: String) : LineParseResult {}