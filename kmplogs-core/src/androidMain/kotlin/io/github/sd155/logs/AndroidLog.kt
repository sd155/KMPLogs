package io.github.sd155.logs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

internal object AndroidLog {
    internal val gson by lazy { Gson() }
    private val _fileLogger = AtomicReference<AndroidFileLogger?>(null)
    private val _logcatLogger = AtomicReference<AndroidLogcatLogger?>(null)
    private val _traceLevelEnabled = AtomicBoolean(false)
    private val _debugLevelEnabled = AtomicBoolean(false)
    private val _androidDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val _uncaughtExceptionHandler = UncaughtExceptionHandler { thread, throwable ->
        try {
            AndroidLogger(sourceTag = "CRASH")
                .fatal(event = "Application crashed", e = throwable)
        }
        catch (_: Exception) { /*ignore failure, as system is already crashed*/ }
        finally {
            _androidDefaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
        }
    }

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
        enableCrashHandler()
    }

    internal fun disableFileLogging() {
        _fileLogger.getAndSet(null)?.close()
        disableCrashHandlerIfNoLoggers()
    }

    internal fun enableLogcatLogging() {
        _logcatLogger.compareAndSet(null, AndroidLogcatLogger())
        enableCrashHandler()
    }

    internal fun disableLogcatLogging() {
        _logcatLogger.set(null)
        disableCrashHandlerIfNoLoggers()
    }

    private fun enableCrashHandler() =
        Thread.setDefaultUncaughtExceptionHandler(_uncaughtExceptionHandler)

    private fun disableCrashHandlerIfNoLoggers() {
        if (_logcatLogger.get() == null && _fileLogger.get() == null)
            Thread.setDefaultUncaughtExceptionHandler(_androidDefaultUncaughtExceptionHandler)
    }

    internal fun enableLevel(type: LogType) =
        when (type) {
            LogType.TRACE -> _traceLevelEnabled.set(true)
            LogType.DEBUG -> _debugLevelEnabled.set(true)
            else -> {}
        }

    internal fun disableLevel(type: LogType) =
        when (type) {
            LogType.TRACE -> _traceLevelEnabled.set(false)
            LogType.DEBUG -> _debugLevelEnabled.set(false)
            else -> {}
        }

    internal fun log(record: JsonObject, sourceTag: String, type: LogType) {
        if (type == LogType.TRACE && !_traceLevelEnabled.get()) return
        if (type == LogType.DEBUG && !_debugLevelEnabled.get()) return
        _fileLogger.get()?.log(record.toString())
        _logcatLogger.get()?.log(type, record, sourceTag)
    }
}