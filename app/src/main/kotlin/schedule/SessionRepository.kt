package schedule

interface SessionRepository {
    fun add(session: Session)
    fun addAll(sessions: List<Session>)
    fun all(): List<Session>
}

object InMemorySessionRepository : SessionRepository  {
    private val bufferSessionsPrivate = mutableListOf<Session>()


    override fun add(session: Session) {
        bufferSessionsPrivate.add(session)
    }

    override fun addAll(sessions: List<Session>) {
        for (session in sessions) bufferSessionsPrivate.add(session)
    }

    override fun all(): List<Session> {
        val sessions: List<Session> = bufferSessionsPrivate.toList()
        return sessions
    }
}

class AuditedSessionRepository (
    private val delegate: SessionRepository
) : SessionRepository by delegate {
    var addCount: Int = 0
        private set

    private val auditEventStorage: MutableList<AuditEvent> = mutableListOf()
    val auditEvent
        get() = auditEventStorage.toList()

    override fun add(session: Session) {
        delegate.add(session)
        addCount++
        auditEventStorage.add(AuditEvent(event = "add", countSessions = 1))
    }

    override fun addAll(sessions: List<Session>) {
        delegate.addAll(sessions)
        val count: Int = sessions.size
        addCount += count
        auditEventStorage.add(AuditEvent(event = "addAll", countSessions = count))
    }
}

data class AuditEvent (
    val event: String,
    val countSessions: Int
)