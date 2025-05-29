# Android File Logging Scheme

## Storage Locations
- **Internal Storage**: Fast and secure storage within the app's private directory
  - Path: `/data/<package_name>/files/logs/`
  - Access: Only accessible by the app
  - Persistence: Cleared on app uninstall
  - Use case: Sensitive logs, temporary debugging

- **External Storage**: Slow but easily accessible storage
  - Path: `/Android/data/<package_name>/files/logs/`
  - Access: Accessible via file manager, ADB
  - Persistence: Remains after app uninstall
  - Use case: Long-term logging, user-accessible logs

## File Structure
```
logs/
├── logs.txt                    # Current active log file
└── <timestamp>_backup_logs.txt # Recent backup log file(s)
```

## File Rotation
- Triggered when current.log reaches `logFileSizeMb` size
- Oldest backup file is deleted if number of backups exceeds `maxBackupFiles`

## Log File Format
Each log entry is a JSON object with the following structure:
```json
{
  "time": "ISO-8601 timestamp",
  "type": "TRACE|DEBUG|INFO|WARNING|ERROR|FATAL",
  "source": "component identifier",
  "thread": "thread name",
  "event": "log message",
  "error": {
    "message": "error message",
    "stacktrace": "stack trace"
  },
  "diagnostics": {
    "ClassA": object,
    "ClassB": object
  }
}
```
