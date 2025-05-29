# KMP Logs - Kotlin Multiplatform Logs Library

Kotlin Multiplatform Logs library. Supporting file and platform specific debug logging.
[Project specification](./PROJECT.md)

## Features

- Android logcat logging
- Android file logging [details](./kmplogs-core/README.md)

## Installation

## Setup

<details>
  <summary>Android</summary>

##### Android Setup

Configure logs before use.
Note, that no logging is enabled by default.
You may reconfigure logs at any time. Note, that you need not to re-instantiate your loggers.

```kotlin
AndroidLoggerConfigurator()
    // Enable file logging.
    .enableFileLogging(
        appContext = context,
        external = false,           // Use internal storage
        maxBackupFiles = 5,        // Keep 5 backup files
        logFileSizeMb = 10         // Rotate at 10MB
    )
    // Enable file logging with defaults: internal, 5 backup files, 10Mb log file threshold.
    .enableFileLogging(appContext = context)
    // Disable file logging.
    .disableFileLogging()
    // Enable logcat logging.
    .enableLogcatLogging()    
    // Disable logcat logging.
    .disableLogcatLogging()

```

To log something - create logger instances and provide them for your components in your favorite DI pattern.

```kotlin
// Use top level factory method to create an instance of Logger.
val logger = createAndroidLogger(sourceTag = "YourComponentTag")
```

</details>

## Usage

After configuring above steps this is how you can use:
```kotlin
// Just use proper Logger methods in your component.
logger.trace(event = "Starting some usecase.")

// To log some exception.
logger.error(event = "Some usecase failure.", e = e)

// You may put some optional objects for diagnostic reason.
logger.debug(event = "Some debug point.", diagnostics = listOf(httpRequest, httpResponse))
```

### Best Practices
1. Use appropriate log levels:
   - TRACE: Detailed debugging
   - DEBUG: General debugging
   - INFO: Normal operations
   - WARNING: Unexpected but handled situations
   - ERROR: Errors that need attention
   - FATAL: Critical errors
2. Include relevant context in diagnostics
3. Use meaningful source tags
4. Enable stack traces for errors
5. Consider storage implications when choosing internal vs external storage

### Security Considerations
1. Internal storage is more secure but less accessible
2. External storage is more accessible but less secure
3. Consider encrypting or cut away sensitive log data, especial sensitive user data.