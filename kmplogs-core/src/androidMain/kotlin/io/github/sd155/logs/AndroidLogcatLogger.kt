package io.github.sd155.logs

import android.util.Log
import com.google.gson.JsonObject

internal class AndroidLogcatLogger {

    internal fun log(type: LogType, record: JsonObject, sourceTag: String) =
        record
            .apply {
                remove(LogRecordKey.TIME)
                remove(LogRecordKey.TYPE)
                remove(LogRecordKey.SOURCE)
            }
            .toString()
            .also { logRecord ->
                when (type) {
                    LogType.TRACE -> Log.v(sourceTag, logRecord)
                    LogType.DEBUG -> Log.d(sourceTag, logRecord)
                    LogType.INFO -> Log.i(sourceTag, logRecord)
                    LogType.WARNING -> Log.w(sourceTag, logRecord)
                    LogType.ERROR -> Log.e(sourceTag, logRecord)
                    LogType.FATAL -> Log.wtf(sourceTag, logRecord)
                }
            }
}