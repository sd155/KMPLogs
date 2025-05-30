package ru.neurospb.logs.api

interface Logger {
    /**
     * A log level describing events showing step by step execution of your code that can be ignored
     * during the standard operation, but may be useful during extended debugging sessions.
     */
    fun trace(event: String, diagnostics: List<Any> = emptyList())
    /**
     * A log level used for events considered to be useful during software debugging when more granular
     * information is needed. The information that is diagnostically helpful to people more than just
     * developers (QA, sysadmins, etc.).
     */
    fun debug(event: String, diagnostics: List<Any> = emptyList())
    /**
     * An event happened, the event is purely informative and can be ignored during normal operations.
     * Some generally useful information to log (service start/stop, configuration assumptions, etc).
     */
    fun info(event: String, diagnostics: List<Any> = emptyList())
    /**
     * Unexpected behavior happened inside the application, but it is continuing its work and the key
     * business features are operating as expected. Such as fallback to secondary strategy, retrying
     * an operation, etc.
     */
    fun warn(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())
    /**
     * One or more functionalities are not working, preventing some functionalities from working correctly.
     * That is any error which is fatal to the operation, but not the application (can't open a required
     * file, unavailable network connection, etc.).
     */
    fun error(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())
    /**
     * One or more key business functionalities are not working and the application does not fulfill
     * the business functionalities (application crash, etc).
     */
    fun fatal(event: String, e: Throwable? = null, diagnostics: List<Any> = emptyList())
}
