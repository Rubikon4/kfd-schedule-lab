package schedule

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionRepositoryTest {
    @Test
    fun addSessionSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = InMemorySessionRepository
        val originalSessions = repository.all()

        repository.add(session)

        assertEquals(originalSessions + session, repository.all())
    }

    @Test
    fun addAllSessionsSuccessKeepOrderAndDuplicates() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val firstSession = Session.Canceled(date = expectedDate, topic = "first topic")
        val secondSession = Session.Canceled(date = expectedDate, topic = "second topic")
        val expectedSessions = listOf(secondSession, firstSession, secondSession)
        val repository = InMemorySessionRepository
        val originalSessions = repository.all()

        repository.addAll(expectedSessions)

        assertEquals(originalSessions + expectedSessions, repository.all())
    }

    @Test
    fun addAllEmptySessionsSuccess() {
        val repository = InMemorySessionRepository
        val expectedSessions = repository.all()

        repository.addAll(emptyList())

        assertEquals(expectedSessions, repository.all())
    }

    @Test
    fun changeOriginalSessionsWithoutChangingRepository() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val sessions = mutableListOf<Session>(session, session)
        val repository = InMemorySessionRepository
        val originalSessions = repository.all()

        repository.addAll(sessions)
        sessions.clear()

        assertEquals(originalSessions + listOf(session, session), repository.all())
    }

    @Test
    fun addSessionWithoutChangingOldSnapshot() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = InMemorySessionRepository
        val snapshot = repository.all()
        val expectedSessions = snapshot.toList()

        repository.add(session)

        assertEquals(expectedSessions, snapshot)
        assertEquals(expectedSessions + session, repository.all())
    }

    @Test
    fun changeSnapshotWithoutChangingRepository() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = InMemorySessionRepository
        repository.addAll(listOf(session, session))
        val expectedSessions = repository.all().toList()

        val snapshot = repository.all() as? MutableList<Session>
        if (snapshot != null) {
            try {
                snapshot.clear()
            } catch (_: UnsupportedOperationException) {
            }
        }

        assertEquals(expectedSessions, repository.all())
    }

    @Test
    fun auditedAddSessionSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val delegate = InMemorySessionRepository
        val originalSessions = delegate.all()
        val repository = AuditedSessionRepository(delegate)

        repository.add(session)

        assertEquals(originalSessions + session, delegate.all())
        assertEquals(delegate.all(), repository.all())
        assertEquals(1, repository.addCount)
        assertEquals(listOf(AuditEvent(event = "add", countSessions = 1)), repository.auditEvent)
    }

    @Test
    fun auditedAddAllSessionsSuccess() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val firstSession = Session.Canceled(date = expectedDate, topic = "first topic")
        val secondSession = Session.Canceled(date = expectedDate, topic = "second topic")
        val expectedSessions = listOf(firstSession, secondSession, firstSession)
        val delegate = InMemorySessionRepository
        val originalSessions = delegate.all()
        val repository = AuditedSessionRepository(delegate)

        repository.addAll(expectedSessions)

        assertEquals(originalSessions + expectedSessions, delegate.all())
        assertEquals(3, repository.addCount)
        assertEquals(listOf(AuditEvent(event = "addAll", countSessions = 3)), repository.auditEvent)
    }

    @Test
    fun auditedAddAllEmptySessionsSuccess() {
        val delegate = InMemorySessionRepository
        val expectedSessions = delegate.all()
        val repository = AuditedSessionRepository(delegate)

        repository.addAll(emptyList())

        assertEquals(expectedSessions, delegate.all())
        assertEquals(0, repository.addCount)
        assertEquals(listOf(AuditEvent(event = "addAll", countSessions = 0)), repository.auditEvent)
    }

    @Test
    fun auditedAddAndAddAllSuccessKeepEventOrder() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = AuditedSessionRepository(InMemorySessionRepository)

        repository.add(session)
        repository.addAll(listOf(session, session, session))
        repository.addAll(emptyList())
        repository.add(session)

        assertEquals(5, repository.addCount)
        assertEquals(
            listOf(
                AuditEvent(event = "add", countSessions = 1),
                AuditEvent(event = "addAll", countSessions = 3),
                AuditEvent(event = "addAll", countSessions = 0),
                AuditEvent(event = "add", countSessions = 1),
            ),
            repository.auditEvent,
        )
    }

    @Test
    fun auditedAllWithoutAddingEvents() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = AuditedSessionRepository(InMemorySessionRepository)
        repository.add(session)
        val expectedEvents = repository.auditEvent

        repository.all()
        repository.all()

        assertEquals(1, repository.addCount)
        assertEquals(expectedEvents, repository.auditEvent)
    }

    @Test
    fun auditedAddWithoutChangingOldEventSnapshot() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = AuditedSessionRepository(InMemorySessionRepository)
        repository.add(session)
        val snapshot = repository.auditEvent

        repository.add(session)

        assertEquals(listOf(AuditEvent(event = "add", countSessions = 1)), snapshot)
        assertEquals(2, repository.auditEvent.size)
    }

    @Test
    fun changeReturnedEventsWithoutChangingAuditJournal() {
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = AuditedSessionRepository(InMemorySessionRepository)
        repository.add(session)
        repository.add(session)
        val expectedEvents = repository.auditEvent.toList()

        val returnedEvents = repository.auditEvent as? MutableList<AuditEvent>
        if (returnedEvents != null) {
            try {
                returnedEvents.clear()
            } catch (_: UnsupportedOperationException) {
            }
        }

        assertEquals(expectedEvents, repository.auditEvent)
        assertEquals(2, repository.addCount)
    }

    @Test
    fun auditedFailedOperationsWithoutAddingEvents() {
        val delegate = object : SessionRepository {
            override fun add(session: Session) {
                throw IllegalStateException("Cannot add session")
            }

            override fun addAll(sessions: List<Session>) {
                throw IllegalStateException("Cannot add sessions")
            }

            override fun all(): List<Session> = emptyList()
        }
        val expectedDate = LocalDate.of(2026, 10, 13)
        val session = Session.Canceled(date = expectedDate, topic = "topic")
        val repository = AuditedSessionRepository(delegate)

        assertFailsWith<IllegalStateException> {
            repository.add(session)
        }
        assertEquals(0, repository.addCount)
        assertEquals(emptyList<AuditEvent>(), repository.auditEvent)

        assertFailsWith<IllegalStateException> {
            repository.addAll(listOf(session))
        }
        assertEquals(0, repository.addCount)
        assertEquals(emptyList<AuditEvent>(), repository.auditEvent)
    }
}