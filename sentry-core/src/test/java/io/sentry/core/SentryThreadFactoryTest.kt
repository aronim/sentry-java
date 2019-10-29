package io.sentry.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class SentryThreadFactoryTest {

    private val sut = SentryThreadFactory()

    @Test
    fun `when getCurrentThreads is called, not empty result`() {
        val threads = sut.currentThreads
        assertNotSame(0, threads.count())
    }

    @Test
    fun `when currentThreadsForCrash is called, current thread is crashed not the others`() {
        val threads = sut.currentThreadsForCrash
        val currentThread = Thread.currentThread()
        val currentSentryThread = threads.first { it.id == currentThread.id }
        assertTrue(currentSentryThread.crashed)
        assertTrue(currentSentryThread.current)
        assertTrue(threads.filter { it.id != currentThread.id }.all { !it.crashed && !it.current })
    }

    @Test
    fun `when currentThreads is called, no thread is marked either crashed or not`() =
        assertTrue(sut.currentThreads.all { it.crashed == null })

    @Test
    fun `when currentThreads is called, thread state is captured`() =
        assertTrue(sut.currentThreads.all { it.state != null })

    @Test
    fun `when currentThreads is called, some thread stack frames are captured`() =
        assertTrue(sut.currentThreads.filter { it.stacktrace != null }.any { it.stacktrace.frames.count() > 0 })
}