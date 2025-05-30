package ru.neurospb.logs

import com.google.gson.Gson
import ru.neurospb.logs.api.Logger

internal class AndroidLogger(
    override val sourceTag: String
) : CommonLogger(), Logger {
    init {
        if (sourceTag.isBlank())
            kotlin.error("Source tag is blank or empty!")
    }

    override fun trace(event: String, diagnostics: List<Any>) =
        process(
            type = LogType.TRACE,
            event = event,
            diagnostics = diagnostics
        )

    override fun debug(event: String, diagnostics: List<Any>) =
        process(
            type = LogType.DEBUG,
            event = event,
            diagnostics = diagnostics
        )

    override fun info(event: String, diagnostics: List<Any>) =
        process(
            type = LogType.INFO,
            event = event,
            diagnostics = diagnostics
        )

    override fun warn(event: String, e: Throwable?, diagnostics: List<Any>) =
        process(
            type = LogType.WARNING,
            event = event,
            throwable = e,
            diagnostics = diagnostics
        )

    override fun error(event: String, e: Throwable?, diagnostics: List<Any>) =
        process(
            type = LogType.ERROR,
            event = event,
            throwable = e,
            diagnostics = diagnostics
        )

    override fun fatal(event: String, e: Throwable?, diagnostics: List<Any>) =
        process(
            type = LogType.FATAL,
            event = event,
            throwable = e,
            diagnostics = diagnostics
        )

    private fun process(
        type: LogType,
        event: String,
        throwable: Throwable? = null,
        includeStackTrace: Boolean = true,
        diagnostics: List<Any> = emptyList()
    ) {
        createRecord(
            type = type,
            event = event,
            throwable = throwable,
            includeStackTrace = includeStackTrace,
            diagnostics = diagnostics
        )
            .also { record -> AndroidLog.log(record, sourceTag, type) }
    }

    override fun serializer(): Gson =
        AndroidLog.gson
}
