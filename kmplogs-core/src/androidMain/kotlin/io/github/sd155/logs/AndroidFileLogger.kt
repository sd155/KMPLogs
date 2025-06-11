package io.github.sd155.logs

import android.content.Context
import android.os.storage.StorageManager
import androidx.annotation.IntRange
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

internal class AndroidFileLogger(
    private val _appContext: Context,
    @IntRange(from = 1, to = 10)
    private val _maxBackedUpFiles: Int,
    external: Boolean,
    @IntRange(from = 1, to = 20)
    thresholdMb: Int,
) {
    private val _logFileName = "logs.txt"
    private val _logDir by lazy {
        if (external) {
            _appContext.getExternalFilesDir(null)
        }
        else {
            _appContext.getDir("logs", Context.MODE_PRIVATE)
        }
            ?: throw IllegalStateException("Logs directory is null!")
    }
    private val _logFile by lazy { File(_logDir, _logFileName) }
    private val _logFileSizeThreshold: Long = thresholdMb.toLong() * 1024 * 1024
    private val _maxFileCheckCounter = 128
    private val _fileCheckCounter = AtomicInteger(_maxFileCheckCounter)
    private var _writer = AtomicReference<PrintWriter?>(null)

    internal fun open() =
        openLogFile()

    internal fun close() =
        setWriter(null)

    @Synchronized
    internal fun log(record: String) {
        if (_fileCheckCounter.getAndDecrement() == 0) {
            _fileCheckCounter.set(_maxFileCheckCounter)
            checkLogFiles()
        }
        //if writer is null, just silently ignore
        _writer.get()?.println(record)
    }

    private fun setWriter(writer: PrintWriter?) =
        _writer.getAndSet(writer)?.close()

    // Let it crash in exception case because that is fatal error case and should not be suppressed.
    private fun openLogFile() {
        val sm = _appContext.getSystemService(StorageManager::class.java)
            ?: throw IllegalStateException("StorageManager service not available")
        val dirUuid = sm.getUuidForPath(_logDir)
        val availableSpace = sm.getAllocatableBytes(dirUuid)
        if (availableSpace > _logFileSizeThreshold) {
            sm.allocateBytes(dirUuid, _logFileSizeThreshold)
            if (!_logFile.exists())
                _logFile.createNewFile()
            val out = FileOutputStream(_logFile, true)
            setWriter(PrintWriter(out, true))
        }
        else {
            //TODO Try to free space and to open log file again. If that fails then throw.
            setWriter(null)
            throw IOException("Insufficient storage space for log file")
        }
    }

    private fun checkLogFiles() {
        if (_logFile.length() > _logFileSizeThreshold) {
            val backup = File(_logDir, "${Instant.now().epochSecond}_backup_$_logFileName")
            if (backup.exists())
                throw IOException("Backup file already exists!")
            if (!_logFile.renameTo(backup))
                throw IOException("Failed to rename log file to backup")
            val logFiles = _logDir.listFiles()
                ?: throw IOException("Failed to list log files")
            logFiles
                .toSortedSet { f1, f2 -> 
                    val diff = f1.lastModified() - f2.lastModified()
                    if (diff < 0) -1 else if (diff > 0) 1 else 0
                }
                .take(positiveOrZero(logFiles.size - _maxBackedUpFiles))
                .forEach { file -> file.delete() }
            openLogFile()
        }
    }

    private fun positiveOrZero(value: Int) = if (value < 0) 0 else value
}