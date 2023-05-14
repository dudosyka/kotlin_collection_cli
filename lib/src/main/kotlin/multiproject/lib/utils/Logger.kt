package multiproject.lib.utils

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.time.ZonedDateTime

class Logger (
    private val logLevel: LogLevel = LogLevel.ERROR,
    logFileDir: String? = System.getProperty("user.dir"),
    logFileName: String? = "logger.file.log"
) {
    private var logFile: PrintWriter? = null
    private lateinit var filePath: String
    init {
        try {
            filePath = "${logFileDir}/${logFileName}"
            if (logLevel < LogLevel.WARN)
                println("Log file created at: $filePath")
            val writerStream = FileWriter(filePath, true)
            this.logFile = PrintWriter(BufferedWriter(writerStream))
            this.logFile!!.println(" --------- Start new session [${this.getCurTimestamp()}] --------- ")
        } catch (e: Exception) {
            //
        }
    }
    private fun getCurTimestamp(): String {
        val date = ZonedDateTime.now()
        return "$date"
    }
    private fun printToFile(message: String) {
        if (logFile != null) {
            logFile!!.close()
            val writerStream = FileWriter(filePath, true)
            this.logFile = PrintWriter(BufferedWriter(writerStream))
            this.logFile!!.println(message)
            this.logFile!!.close()
        }
    }
    operator fun invoke(level: LogLevel, message: String? = null, error: Exception? = null) {
        var logString = "[${this.getCurTimestamp()}] $message"
        if (error != null)
            logString += "\n$error \n{${error.stackTraceToString()}}"
        if (level >= logLevel) {
            println(logString)
        }
        if (logFile != null) {
            this.printToFile(logString)
        }
    }
}