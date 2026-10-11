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
        private val teachersPrivate: List<String> = teachersRaw.toList()
        val teachers: List<String>
            get() = teachersPrivate.toList()

                init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if (place == "-") throw InvalidArgumentsException()
            validateRawTeacher(teachersPrivate)
        }
    }

    class Practice(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        teachersRaw: List<String>
    ) : Session {
        private val teachersPrivate: List<String> = teachersRaw.toList()
        val teachers: List<String>
            get() = teachersPrivate.toList()

        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if (place == "-") throw InvalidArgumentsException()
            validateRawTeacher(teachersPrivate)
        }
    }

    class Exam(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        teachersRaw: List<String>
    ) : Session {
        private val teachersPrivate: List<String> = teachersRaw.toList()
        val teachers: List<String>
            get() = teachersPrivate.toList()

        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if (place == "-") throw InvalidArgumentsException()
            validateRawTeacher(teachersPrivate)
        }
    }

    class Cancel(
        override val date: LocalDate,
        override val topic: String
    ) : Session {
        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
        }
    }
}

fun validateRawInput(input: String) {
    if (
        input.isBlank() ||
        input != input.trim() ||
        input.any { symbol -> Character.isISOControl(symbol.code) }
    ) throw InvalidArgumentsException()

}

fun validateRawTeacher(list: List<String>) {
    if (list.size != list.distinct().size) throw InvalidArgumentsException()
    for (name in list) {
        validateRawInput(name)
        if (name == "-") throw InvalidArgumentsException()
    }
}