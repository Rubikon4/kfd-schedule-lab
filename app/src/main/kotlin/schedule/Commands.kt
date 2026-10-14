package schedule

interface CommandHandlers {
    fun period(command: Command.Period): Int
    fun byType(command: Command.ByType): Int
    fun errors(command: Command.Errors): Int
}

object StudentCommandHandlers : CommandHandlers {
    override fun period(command: Command.Period): Int = runPeriod(command)
    override fun byType(command: Command.ByType): Int = runType(command)
    override fun errors(command: Command.Errors): Int = runErrors(command)
}

fun runPeriod(command: Command.Period): Int {
    val parser = ScheduleLineParser()
    var selectedSessions = 0L
    var rejectedLines = 0L

    forEachScheduleLine(command.file) { number, line ->
        when (val result = parser.scheduleParseLine(number, line)) {
            is IncorrectLine -> rejectedLines++
            is CorrectLine -> {
                val session = result.session
                if (session.date >= command.from && session.date <= command.to) {
                    val fields = when (session) {
                        is Session.Lecture -> "LECTURE | ${session.topic} | ${session.place}"
                        is Session.Practice -> "PRACTICE | ${session.topic} | ${session.place}"
                        is Session.Exam -> "EXAM | ${session.topic} | ${session.place}"
                        is Session.Canceled -> "CANCELLED | ${session.topic} | -"
                    }
                    println("${session.date} | $fields")
                    selectedSessions++
                }
            }
        }
    }

    println("Total: $selectedSessions")
    return if (rejectedLines == 0L) 0 else 1
}

fun runType(command: Command.ByType): Int {
    val parser = ScheduleLineParser()
    var selectedSessions = 0L
    var rejectedLines = 0L

    forEachScheduleLine(command.file) { number, line ->
        when (val result = parser.scheduleParseLine(number, line)) {
            is IncorrectLine -> rejectedLines++
            is CorrectLine -> {
                val session = result.session
                val type = when (session) {
                    is Session.Lecture -> SessionType.LECTURE
                    is Session.Practice -> SessionType.PRACTICE
                    is Session.Exam -> SessionType.EXAM
                    is Session.Canceled -> SessionType.CANCELLED
                }
                if (type == command.type) {
                    val details = when (session) {
                        is Session.Lecture -> "${session.place} | ${session.teachers.joinToString(", ")}"
                        is Session.Practice -> "${session.place} | ${session.teachers.joinToString(", ")}"
                        is Session.Exam -> "${session.place} | ${session.teachers.joinToString(", ")}"
                        is Session.Canceled -> "- | -"
                    }
                    println("${session.date} | ${type.name} | ${session.topic} | $details")
                    selectedSessions++
                }
            }
        }
    }

    println("Total: $selectedSessions")
    return if (rejectedLines == 0L) 0 else 1
}

fun runErrors(command: Command.Errors): Int {
    val parser = ScheduleLineParser()
    var totalLines = 0L
    var rejectedLines = 0L

    forEachScheduleLine(command.file) { number, line ->
        totalLines++
        when (val result = parser.scheduleParseLine(number, line)) {
            is CorrectLine -> Unit
            is IncorrectLine -> {
                rejectedLines++
                println(formatLineError(result))
            }
        }
    }

    println("Rejected: $rejectedLines of $totalLines")
    return if (rejectedLines == 0L) 0 else 1
}
