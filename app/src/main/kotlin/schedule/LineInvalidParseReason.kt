package schedule

sealed interface LineInvalidParseReason {}

/** The line is missing the field separator |. */
data object InvalidFieldSeparator : LineInvalidParseReason

/** The number of fields differs from five. */
data class InvalidFieldCount(val actualCount: Int) : LineInvalidParseReason

/** The specified field is empty or contains only whitespace characters. */
data class EmptyRequiredField(val fieldName: String) : LineInvalidParseReason

/** Invalid date format, a nonexistent date, or a year outside the range 1..9999. */
data class InvalidDate(val value: String) : LineInvalidParseReason

/** The session type is not in the list of allowed values. */
data class UnknownSessionType(val value: String) : LineInvalidParseReason

/** The exam has fewer than two names that are nonempty, not whitespace-only, and different from -. */
data class InsufficientExamTeachers(val actualCount: Int) : LineInvalidParseReason

/** Control characters were found in the specified field. */
data class ControlCharacters(val fieldName: String, val value: String) : LineInvalidParseReason

/** The value has leading or trailing whitespace characters. */
data class OuterWhitespace(val fieldName: String, val value: String) : LineInvalidParseReason

/** The location or teachers for a cancelled session are not specified as -. */
data class InvalidCancelledField(val fieldName: String, val value: String) : LineInvalidParseReason

/** The location of an active session is invalid, for example, equal to -. */
data class InvalidLocation(val value: String) : LineInvalidParseReason

/** The teacher's name is empty, whitespace-only, or equal to -; element numbering starts at 1. */
data class InvalidTeacher(val teacherNumber: Int,val value: String) : LineInvalidParseReason

/** The teacher's name is duplicated; the duplicate element's number is 1-based. */
data class DuplicateTeacher(val teacherNumber: Int, val value: String) : LineInvalidParseReason
