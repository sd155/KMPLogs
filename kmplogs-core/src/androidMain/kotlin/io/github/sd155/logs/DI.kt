package io.github.sd155.logs

import android.content.Context
import io.github.sd155.logs.api.Logger

/**
 * Configuration class for Android logging functionality.
 * Provides methods to enable and disable different logging mechanisms (file and logcat).
 *
 * Default Configuration
 * - TRACE level is disabled by default
 * - DEBUG level is disabled by default
 * - INFO, WARNING, ERROR, and FATAL levels are always enabled
 * - File logging is disabled by default
 * - Logcat logging is disabled by default
 */
class AndroidLoggerConfigurator {

    /**
     * Enables file-based logging for the application.
     *
     * @param appContext The application context used to access storage locations
     * @param external Whether to store logs in external slow and easily accessible storage (true) or internal fast and secure storage (false)
     * @param maxBackupFiles Maximum number of backup log files to keep before rotation
     * @param logFileSizeMb Maximum size of each log file in megabytes before rotation
     */
    fun enableFileLogging(
        appContext: Context,
        external: Boolean = false,
        maxBackupFiles: Int = 5,
        logFileSizeMb: Int = 10,
    ): AndroidLoggerConfigurator =
        AndroidLog.enableFileLogging(
            appContext = appContext,
            external = external,
            maxBackupFiles = maxBackupFiles,
            logFileSizeMb = logFileSizeMb,
        ).let { this }

    /**
     * Disables file-based logging.
     * Any existing log files will remain but no new logs will be written to files.
     */
    fun disableFileLogging(): AndroidLoggerConfigurator =
        AndroidLog.disableFileLogging().let { this }

    /**
     * Enables logging to Android's logcat system.
     * Logs will be visible in Android Studio's logcat window and through adb logcat.
     */
    fun enableLogcatLogging(): AndroidLoggerConfigurator =
        AndroidLog.enableLogcatLogging().let { this }

    /**
     * Disables logging to Android's logcat system.
     * Logs will no longer be visible in Android Studio's logcat window or through adb logcat.
     */
    fun disableLogcatLogging(): AndroidLoggerConfigurator =
        AndroidLog.disableLogcatLogging().let { this }

    /**
     * Enables TRACE level logging.
     * TRACE is the most detailed logging level, typically used for:
     * - Detailed method entry/exit logging
     * - Variable state tracking
     * - Detailed flow control information
     * - Performance measurements
     *
     * This level is usually disabled in production environments.
     */
    fun enableTraceLogging(): Unit =
        AndroidLog.enableLevel(LogType.TRACE)

    /**
     * Disables TRACE level logging.
     * After calling this method, all trace logs will be ignored.
     */
    fun disableTraceLogging(): Unit =
        AndroidLog.disableLevel(LogType.TRACE)

    /**
     * Enables DEBUG level logging.
     * DEBUG level is used for detailed information useful during development and troubleshooting:
     * - Function results
     * - State changes
     * - Configuration values
     * - Detailed operation progress
     *
     * This level is typically enabled in development and testing environments.
     */
    fun enableDebugLogging(): Unit =
        AndroidLog.enableLevel(LogType.DEBUG)

    /**
     * Disables DEBUG level logging.
     * After calling this method, all debug logs will be ignored.
     */
    fun disableDebugLogging(): Unit =
        AndroidLog.disableLevel(LogType.DEBUG)
}

/**
 * Creates a new logger instance with the specified source tag.
 *
 * @param sourceTag A string identifier for the source of the logs (e.g., component or class name)
 * @return A new [Logger] instance configured for Android
 */
fun createAndroidLogger(sourceTag: String): Logger =
    AndroidLogger(sourceTag)