package io.github.sd155.logs

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

internal object LogRecordKey {
    const val TIME = "time"
    const val TYPE = "type"
    const val SOURCE = "source"
    const val THREAD = "thread"
    const val EVENT = "event"
    const val ERROR = "error"
    const val ERROR_MESSAGE = "message"
    const val ERROR_STACK_TRACE = "stacktrace"
    const val DIAGNOSTICS = "diagnostics"
}

internal enum class LogType { TRACE, DEBUG, INFO, WARNING, ERROR, FATAL }

internal abstract class CommonLogger {
    protected abstract val sourceTag: String

    protected fun createRecord(
        type: LogType,
        event: String,
        throwable: Throwable?,
        includeStackTrace: Boolean,
        diagnostics: List<Any>,
    ): JsonObject =
        JsonObject().also { record ->
            record.addProperty(LogRecordKey.TIME, Instant.now().toString())
            record.addProperty(LogRecordKey.TYPE, type.name)
            record.addProperty(LogRecordKey.SOURCE, sourceTag)
            record.addProperty(LogRecordKey.THREAD, Thread.currentThread().name)
            record.addProperty(LogRecordKey.EVENT, event)
            throwable?.let { throwable ->
                JsonObject()
                    .also { error ->
                        error.addProperty(LogRecordKey.ERROR_MESSAGE, throwable.toString())
                        if (includeStackTrace) {
                            val stackTraceAsString = StringWriter()
                                .also { writer -> throwable.printStackTrace(PrintWriter(writer)) }
                                .toString()
                            error.addProperty(LogRecordKey.ERROR_STACK_TRACE, stackTraceAsString)
                        }
                        record.add(LogRecordKey.ERROR, error)
                    }
            }
            if (diagnostics.isNotEmpty()) {
                val diagnosticsByClass = JsonObject()
                diagnostics.forEach {
                    val key = it::class.simpleName ?: "Object"
                    val jsonElement = JsonParser.parseString(serializer().toJson(it))
                    diagnosticsByClass.add(key, jsonElement)
                }
                record.add(LogRecordKey.DIAGNOSTICS, diagnosticsByClass)
            }
        }

    protected abstract fun serializer(): Gson
}