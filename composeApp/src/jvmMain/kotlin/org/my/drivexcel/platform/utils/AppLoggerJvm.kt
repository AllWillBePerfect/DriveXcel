package org.my.drivexcel.platform.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.my.drivexcel.platform.AppLogger
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppLoggerJvm : AppLogger {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun d(tag: String, message: String) =
        println(
            logFormat(
                level = "DEBUG",
                tag = tag,
                message = message
            )
        )

    override fun i(tag: String, message: String) =
        println(
            logFormat(
                level = "INFO",
                tag = tag,
                message = message
            )
        )

    override fun e(tag: String, message: String, throwable: Throwable?) =
        println(
            logFormat(
                level = "ERROR",
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    internal fun logFormat(
        level: String,
        tag: String,
        message: String,
        throwable: Throwable? = null
    ): String {
        val levelColored = colorize(level)
        val timestamp = LocalDateTime.now().format(formatter)
        val base = "$timestamp $levelColored: [$tag] $message"
        return if (throwable != null) "$base\n${throwable.stackTraceToString()}" else base
    }

    private fun colorize(level: String): String = when (level) {
        "ERROR" -> "\u001B[31m$level\u001B[0m" // красный
        "INFO" -> "\u001B[34m$level\u001B[0m" // синий
        "DEBUG" -> "\u001B[32m$level\u001B[0m" // зелёный
        else -> level
    }
}

class AppLoggerWithLocalFileJvm(
    private val consoleLogger: AppLoggerJvm,
) : AppLogger {
    private val logFile: File
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    init {
        val logDir =
            Paths.get(
                System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
                AppLogger.JVM_FOLDER_DIRECTORY_NAME
            ).toFile()
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        logFile = File(logDir, AppLogger.JVM_LOGGER_FILE_NAME)
    }


    /*
        private val logFile: File = File(logFilePath).apply {
            parentFile?.mkdirs()
            if (!exists()) createNewFile()
        }*/

    override fun d(tag: String, message: String) {
        logToAll("DEBUG", tag, message)
    }

    override fun i(tag: String, message: String) {
        logToAll("INFO", tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        logToAll("ERROR", tag, message, throwable)
    }

    private fun logToAll(
        level: String,
        tag: String,
        message: String,
        throwable: Throwable? = null
    ) {
        val formatted = consoleLogger.run {
            logFormat(level, tag, message, throwable)
        }
        println(formatted)

        val plainText = formatted.replace(Regex("\u001B\\[[;\\d]*m"), "")
        writeToFile(plainText)
    }

    private fun writeToFile(text: String) {
        try {
            FileWriter(logFile, true).use { writer ->
                writer.appendLine(text)
            }
        } catch (e: IOException) {
            println("Logger error: ${e.message}")
        }
    }
}