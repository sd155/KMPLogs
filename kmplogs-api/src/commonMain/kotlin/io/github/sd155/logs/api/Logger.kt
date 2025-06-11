package io.github.sd155.logs.api

/**
 * Interface for logging events and diagnostics across different severity levels.
 * This interface provides methods for logging with varying levels of severity,
 * from trace (least severe) to fatal (most severe).
 *
 * Severity levels are ordered as follows (from least to most severe):
 * 1. TRACE - Most detailed debugging information
 * 2. DEBUG - Detailed information for development
 * 3. INFO - General operational information
 * 4. WARN - Potentially harmful situations
 * 5. ERROR - Error events that might still allow the application to continue
 * 6. FATAL - Very severe error events that will lead to application termination
 *
 * Each level serves a specific purpose in application monitoring and debugging:
 * - TRACE and DEBUG levels are primarily used during development
 * - INFO level is used for normal operation monitoring
 * - WARN, ERROR, and FATAL levels are used for production issue tracking
 */
interface Logger {
    /**
     * Logs a trace level event with optional diagnostics.
     * Trace level is the most detailed logging level, typically used for:
     * - Detailed method entry/exit logging
     * - Variable state tracking
     * - Detailed flow control information
     * - Performance measurements
     *
     * Use this level when you need the most detailed debugging information.
     * This level is usually disabled in production environments.
     *
     * @param event The event message to log
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun trace(event: String, diagnostics: List<Any> = emptyList())

    /**
     * Logs a debug level event with optional diagnostics.
     * Debug level is used for detailed information useful during development and troubleshooting:
     * - Function results
     * - State changes
     * - Configuration values
     * - Detailed operation progress
     *
     * Use this level for information that is helpful for debugging but not as verbose as trace.
     * This level is typically enabled in development and testing environments.
     *
     * @param event The event message to log
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun debug(event: String, diagnostics: List<Any> = emptyList())

    /**
     * Logs an info level event with optional diagnostics.
     * Info level is used for general operational information that highlights the progress of the application:
     * - Application startup and shutdown
     * - Major state changes
     * - Successful completion of significant operations
     * - Important business events
     *
     * Use this level for information that is useful for monitoring the application's normal operation.
     * This level is typically enabled in all environments.
     *
     * @param event The event message to log
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun info(event: String, diagnostics: List<Any> = emptyList())

    /**
     * Logs a warning level event with optional exception and diagnostics.
     * Warning level is used for potentially harmful situations that don't prevent the application from working:
     * - Deprecated API usage
     * - Retry attempts
     * - Performance issues
     * - Unexpected but handled conditions
     *
     * Use this level when something unexpected happens but the application can continue to function.
     * This level is typically enabled in all environments.
     *
     * @param event The event message to log
     * @param e Optional throwable that caused the warning
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun warn(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())

    /**
     * Logs an error level event with optional exception and diagnostics.
     * Error level is used for error events that might still allow the application to continue running:
     * - Failed operations
     * - Database connection issues
     * - API call failures
     * - Resource exhaustion
     *
     * Use this level when an error occurs that affects a specific operation but doesn't necessarily
     * prevent the application from continuing to function.
     * This level is typically enabled in all environments.
     *
     * @param event The event message to log
     * @param e Optional throwable that caused the error
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun error(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())

    /**
     * Logs a fatal level event with optional exception and diagnostics.
     * Fatal level is used for very severe error events that will presumably lead the application to abort:
     * - Unrecoverable system errors
     * - Critical resource failures
     * - Security violations
     * - Data corruption
     *
     * Use this level when an error occurs that is so severe that the application cannot continue
     * to function and must terminate.
     * This level is typically enabled in all environments and should trigger immediate attention.
     *
     * @param event The event message to log
     * @param e Optional throwable that caused the fatal error
     * @param diagnostics Optional list of diagnostic information to include with the log
     */
    fun fatal(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())
}
