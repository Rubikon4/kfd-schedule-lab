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
/**
class AuditedSessionRepository (
    private val delegate: SessionRepository
) : SessionRepository by delegate {
    private val sessionSucceesAddOperatons: MutableMap<String, Session>
    private val sessionsAddCount: Int
}
        */