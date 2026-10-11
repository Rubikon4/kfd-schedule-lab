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
        val teacher: MutableList<String>
    ) : Session {
        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if ("-" in place) throw InvalidArgumentsException()
        }
    }

    class Practice(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        val teacher: MutableList<String>
    ) : Session {
        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if ("-" in place) throw InvalidArgumentsException()
        }
    }

    class Exam(
        override val date: LocalDate,
        override val topic: String,
        val place: String,
        val teacher: MutableList<String>
    ) : Session {
        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
            validateRawInput(topic)
            validateRawInput(place)
            if ("-" in place) throw InvalidArgumentsException()
        }
    }

    class Cancel(
        override val date: LocalDate,
        override val topic: String
    ) : Session {
        init {
            if (date.year !in 1..9999) throw InvalidArgumentsException()
        }
    }
}

fun validateRawInput(input: String) {
    require(
        input.isNotBlank() &&
                input == input.trim() &&
                !input.any { symbol -> Character.isISOControl(symbol.code) }
    ) { InvalidArgumentsException() }
}

fun validateRawTeacher(list: List<String>) {
    require(list.size == list.distinct().size ) { InvalidArgumentsException() }
    for (name in list) {
        require(
            name.isNotBlank() &&
                    "-" !in name &&
                    name == name.trim()
        )
        { InvalidArgumentsException() }
    }
}