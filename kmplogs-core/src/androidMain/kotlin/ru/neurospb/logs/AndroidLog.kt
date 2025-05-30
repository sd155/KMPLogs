package ru.neurospb.logs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.concurrent.atomic.AtomicReference

internal object AndroidLog {
    internal val gson by lazy { Gson() }
    private val _fileLogger = AtomicReference<AndroidFileLogger?>(null)
    private val _logcatLogger = AtomicReference<AndroidLogcatLogger?>(null)

    internal fun enableFileLogging(
        appContext: Context,
        external: Boolean,
        maxBackupFiles: Int,
        logFileSizeMb: Int,
    ) {
        _fileLogger.compareAndSet(
            null,
            AndroidFileLogger(
                _appContext = appContext,
                external = external,
                _maxBackedUpFiles = maxBackupFiles,
                thresholdMb = logFileSizeMb,
            ).apply { open() }
        )
    }

    internal fun disableFileLogging() {
        _fileLogger.getAndSet(null)?.close()
    }

    internal fun enableLogcatLogging() {
        _logcatLogger.compareAndSet(null, AndroidLogcatLogger())
    }

    internal fun disableLogcatLogging() {
        _logcatLogger.set(null)
    }

    internal fun log(record: JsonObject, sourceTag: String, type: LogType) {
        _fileLogger.get()?.log(record.toString())
        _logcatLogger.get()?.log(type, record, sourceTag)
    }
}