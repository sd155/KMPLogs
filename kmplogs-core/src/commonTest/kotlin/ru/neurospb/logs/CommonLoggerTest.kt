package ru.neurospb.logs

import com.google.gson.Gson
import kotlin.test.*

private class TestLogger(tag: String) : CommonLogger() {
    override val sourceTag: String = tag

    override fun serializer(): Gson = Gson()

    fun createRecordTest(
        type: LogType,
        event: String,
        throwable: Throwable?,
        includeStackTrace: Boolean,
        diagnostics: List<Any>,
    ) =
        createRecord(type, event, throwable, includeStackTrace, diagnostics)
}

private class D1 {
    val fieldA = "public_field"
    private val _fieldB = "private_field"
    val fieldC = 5

    fun nothing() {}
}

private data class D2(
    val valueS: String = "Some value",
    val valueN: Int = 55,
)

internal class CommonLoggerTest {
    private val _tag = "TestLogger"
    private val _logger = TestLogger(_tag)

    @Test
    fun createRecordAllFields() {
        val event = "Test message"
        val record = _logger.createRecordTest(
            type = LogType.INFO,
            event = event,
            throwable = null,
            includeStackTrace = false,
            diagnostics = emptyList()
        )

        assertNotNull(record.get(LogRecordKey.TIME))
        assertEquals(LogType.INFO.name, record.get(LogRecordKey.TYPE).asString)
        assertEquals(_tag, record.get(LogRecordKey.SOURCE).asString)
        assertNotNull(record.get(LogRecordKey.THREAD))
        assertEquals(event, record.get(LogRecordKey.EVENT).asString)
        assertFalse(record.has(LogRecordKey.ERROR))
        assertFalse(record.has(LogRecordKey.DIAGNOSTICS))
    }

    @Test
    fun createRecordWithError() {
        val event = "Error occurred"
        val exception = RuntimeException("Test error")
        val record = _logger.createRecordTest(
            type = LogType.ERROR,
            event = event,
            throwable = exception,
            includeStackTrace = true,
            diagnostics = emptyList()
        )

        val error = record.getAsJsonObject(LogRecordKey.ERROR)
        assertNotNull(error)
        assertEquals("java.lang.RuntimeException: Test error", error.get(LogRecordKey.ERROR_MESSAGE).asString)
        assertTrue(error.has(LogRecordKey.ERROR_STACK_TRACE))
    }

    @Test
    fun createRecordWithDiagnostics1() {
        val diagnostics = listOf(D1(), D2())
        val record = _logger.createRecordTest(
            type = LogType.DEBUG,
            event = "Debug message",
            throwable = null,
            includeStackTrace = false,
            diagnostics = diagnostics
        )

        val diagnosticsObj = record.getAsJsonObject(LogRecordKey.DIAGNOSTICS)
        assertNotNull(diagnosticsObj)
    }

    @Test
    fun createRecordWithEmptyDiagnostics() {
        val record = _logger.createRecordTest(
            type = LogType.INFO,
            event = "Test message",
            throwable = null,
            includeStackTrace = false,
            diagnostics = emptyList()
        )

        assertFalse(record.has(LogRecordKey.DIAGNOSTICS))
    }

    @Test
    fun createRecordWithNullThrowable() {
        val record = _logger.createRecordTest(
            type = LogType.INFO,
            event = "Test message",
            throwable = null,
            includeStackTrace = false,
            diagnostics = emptyList()
        )

        assertFalse(record.has(LogRecordKey.ERROR))
    }
} 