# KMP Logs - Kotlin Multiplatform Logs Library

Kotlin Multiplatform Logs library. Supporting file and platform specific debug logging.
[Project specification](./PROJECT.md)

## Features

- JSON-formatted log entries
- Android logcat logging
- Android file logging [details](./kmplogs-core/README.md)
- Granular control over logging levels (TRACE and DEBUG)
- Configurable Android log storage (internal/external)
- Android automatic crash logging

## Installation

KMPLogs is available on Maven Central. In your root project `build.gradle.kts` file (or `settings.gradle` file) add `mavenCentral()` to repositories.

```kotlin
repositories { 
  mavenCentral()
}
```

Then in your modules add kmplogs-api dependency. Latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.sd155/kmplogs-api?color=blue)](https://search.maven.org/search?q=g:io.github.sd155+kmplogs).
```kotlin
sourceSets {
  commonMain.dependencies {
    implementation("io.github.sd155:kmplogs-api:<version>") //Lightweight Logger API
  }
}
```
And in your application layer module add kmplogs-core dependency. Latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.sd155/kmplogs-core?color=blue)](https://search.maven.org/search?q=g:io.github.sd155+kmplogs).
```kotlin
sourceSets {
  androidMain.dependencies {
    implementation("io.github.sd155:kmplogs-core:<version>") //Logs implementation for Android
  }
}
```

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
    // Enable TRACE level logging (most detailed).
    .enableTraceLogging()
    // Disable TRACE level logging.
    .disableTraceLogging()
    // Enable DEBUG level logging.
    .enableDebugLogging()
    // Disable DEBUG level logging.
    .disableDebugLogging()
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
### Default Configuration
- TRACE level is disabled by default
- DEBUG level is disabled by default
- INFO, WARNING, ERROR, and FATAL levels are always enabled
- File logging is disabled by default
- Logcat logging is disabled by default

**Note:** To enable any logging, you must explicitly configure it using `AndroidLoggerConfigurator`.

### Best Practices
1. Use appropriate log levels:
   - TRACE: Detailed debugging (method entry/exit, variable state, flow control)
   - DEBUG: General debugging (function results, state changes, configuration)
   - INFO: Normal operations (startup, shutdown, major state changes)
   - WARNING: Unexpected but handled situations (deprecated API, retries)
   - ERROR: Errors that need attention (failed operations, connection issues)
   - FATAL: Critical errors (unrecoverable system errors, security violations)
2. Include relevant context in diagnostics
3. Use meaningful source tags
4. Enable stack traces for errors
5. Consider storage implications when choosing internal vs external storage
6. Control TRACE and DEBUG levels based on environment:
   - Enable in development for detailed debugging
   - Disable in production to reduce overhead
   - Consider enabling temporarily for troubleshooting

### Security Considerations
1. Internal storage is more secure but less accessible
2. External storage is more accessible but less secure
3. Consider encrypting or cut away sensitive log data, especial sensitive user data.

## Log record scheme

Each log entry is stored as a JSON object with the following structure:

```json
{
  "time": "ISO-8601 timestamp",
  "type": "TRACE|DEBUG|INFO|WARNING|ERROR|FATAL",
  "source": "component identifier",
  "thread": "thread name",
  "event": "event/message",
  "error": {
    "message": "error message",
    "stacktrace": "stack trace"
  },
  "diagnostics": {
    "class name": { serialized to JSON instance of class }
  }
}
```
