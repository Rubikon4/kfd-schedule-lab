package schedule

sealed interface LineInvalidParseReason {}

data object InvalidFieldSeparator : LineInvalidParseReason

data class InvalidFieldCount(val actualCount: Int) : LineInvalidParseReason

data class EmptyRequiredField(val fieldName: String) : LineInvalidParseReason

data class InvalidDate(val value: String) : LineInvalidParseReason

data class UnknownSessionType(val value: String) : LineInvalidParseReason

data class InsufficientExamTeachers(val actualCount: Int) : LineInvalidParseReason

data class ControlCharacters(val fieldName: String, val value: String) : LineInvalidParseReason

data class OuterWhitespace(val fieldName: String, val value: String) : LineInvalidParseReason

data class InvalidCancelledField(val fieldName: String, val value: String) : LineInvalidParseReason

data class InvalidLocation(val value: String) : LineInvalidParseReason

data class InvalidTeacher(val teacherNumber: Int,val value: String) : LineInvalidParseReason

data class DuplicateTeacher(val teacherNumber: Int, val value: String) : LineInvalidParseReason
