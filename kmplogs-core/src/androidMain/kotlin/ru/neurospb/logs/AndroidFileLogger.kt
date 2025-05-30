package ru.neurospb.logs

import android.content.Context
import android.os.storage.StorageManager
import androidx.annotation.IntRange
import java.io.File
import java.io.FileOutputStream
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

    internal fun open() {
        if (_logFile.exists())
            openLogFile()
        else
            createLogFile()
    }

    internal fun close() = setWriter(null)

    @Synchronized
    internal fun log(record: String) {
        if (_fileCheckCounter.getAndDecrement() == 0) {
            _fileCheckCounter.set(_maxFileCheckCounter)
            checkLogFiles()
        }
        _writer.get()?.println(record)
    }

    private fun setWriter(writer: PrintWriter?) {
        val old = _writer.getAndSet(writer)
        if (writer == null)
            old?.close()
    }

    private fun createLogFile() {
        val sm = _appContext.getSystemService(StorageManager::class.java)
        val dirUuid = sm.getUuidForPath(_logDir)
        val availableSpace = sm.getAllocatableBytes(dirUuid)
        if (availableSpace > _logFileSizeThreshold) {
            sm.allocateBytes(dirUuid, _logFileSizeThreshold)
            _logFile.createNewFile()
            openLogFile()
        }
        else {
            setWriter(null)
        }
    }

    private fun openLogFile() {
        val out = FileOutputStream(_logFile, true)
        setWriter(PrintWriter(out, true))
    }

    private fun checkLogFiles() {
        if (_logFile.length() > _logFileSizeThreshold) {
            val backup = File(_logDir, "${Instant.now().epochSecond}_backup_$_logFileName")
            _logFile.renameTo(backup)
            val logFiles = _logDir.listFiles()
            logFiles
                ?.toSortedSet { f1, f2 -> (f1.lastModified() - f2.lastModified()).toInt() }
                ?.take(positiveOrZero(logFiles.size - _maxBackedUpFiles))
                ?.forEach { it.delete() }
            createLogFile()
        }
    }

    private fun positiveOrZero(value: Int) = if (value < 0) 0 else value
}