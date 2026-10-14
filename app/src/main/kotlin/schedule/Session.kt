package schedule

import java.time.LocalDate

/** Domain model of the lesson. */
sealed interface Session {
    val date: LocalDate
    val topic: String

    class Lecture(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        teachersRaw: List<String>
    ) : Session {
        private val teachersPrivate: List<String> = java.util.List.copyOf(teachersRaw)
        val teachers: List<String>
            get() = teachersPrivate

        init {
            if (date.year !in 1..9999) throw IllegalArgumentException("date year must be between 1 and 9999")
            validateRawInput(topic, "topic")
            validateRawInput(place, "place")
            if (place == "-") throw IllegalArgumentException("place must not be '-'")
            if (teachersPrivate.isEmpty()) throw IllegalArgumentException("LECTURE requires at least 1 teacher")
            validateRawTeacher(teachersPrivate)
        }
    }

    class Practice(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        teachersRaw: List<String>
    ) : Session {
        private val teachersPrivate: List<String> = java.util.List.copyOf(teachersRaw)
        val teachers: List<String>
            get() = teachersPrivate

        init {
            if (date.year !in 1..9999) throw IllegalArgumentException("date year must be between 1 and 9999")
            validateRawInput(topic, "topic")
            validateRawInput(place, "place")
            if (place == "-") throw IllegalArgumentException("place must not be '-'")
            if (teachersPrivate.isEmpty()) throw IllegalArgumentException("PRACTICE requires at least 1 teacher")
            validateRawTeacher(teachersPrivate)
        }
    }

    class Exam(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        teachersRaw: List<String>
    ) : Session {
        private val teachersPrivate: List<String> = java.util.List.copyOf(teachersRaw)
        val teachers: List<String>
            get() = teachersPrivate

        init {
            if (date.year !in 1..9999) throw IllegalArgumentException("date year must be between 1 and 9999")
            validateRawInput(topic, "topic")
            validateRawInput(place, "place")
            if (place == "-") throw IllegalArgumentException("place must not be '-'")
            if (teachersPrivate.size < 2) throw IllegalArgumentException("EXAM requires at least 2 teachers")

            validateRawTeacher(teachersPrivate)
        }
    }

    class Canceled(
        override val date: LocalDate,
        override val topic: String
    ) : Session {
        init {
            if (date.year !in 1..9999) throw IllegalArgumentException("date year must be between 1 and 9999")
            validateRawInput(topic, "topic")
        }
    }
}

fun validateRawInput(input: String, fieldName: String = "value") {
    if (input.isBlank()) throw IllegalArgumentException("$fieldName must not be blank")
    if (input != input.trim()) {
        throw IllegalArgumentException("$fieldName must not have leading or trailing whitespace")
    }
    if (input.any { symbol -> Character.isISOControl(symbol.code) }) { // проверяет ли isISOControl /n?
        throw IllegalArgumentException("$fieldName must not contain control characters")
    }
}

fun validateRawTeacher(list: List<String>) {
    if (list.size != list.distinct().size) throw IllegalArgumentException("teacher names must be unique")
    for ((index, name) in list.withIndex()) {
        val fieldName = "teacher ${index + 1}"
        validateRawInput(name, fieldName)
        if (name == "-") throw IllegalArgumentException("$fieldName must not be '-'")
    }
}
