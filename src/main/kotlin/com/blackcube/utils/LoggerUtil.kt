package com.blackcube.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LoggerUtil {
    private val logger = KotlinLogging.logger {}
    private val logDir: Path = Path.of("logs")

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        try {
            Files.createDirectories(logDir)
        } catch (e: IOException) {
            logger.error(e) { "Не удалось создать директорию логов: $logDir" }
        }
    }

    /**
     * Записывает сообщение:
     * 1) в стандартный лог (KotlinLogging)
     * 2) в файл за текущий день `logs/<today>.txt`
     *
     * @param message — текст лога
     */
    fun log(message: String) {
        val now       = LocalDateTime.now()
        val dateStr   = now.toLocalDate().format(dateFormatter)      // e.g. "2025-05-26"
        val fileName  = "$dateStr.txt"                              // e.g. "2025-05-26.txt"
        val logFile   = logDir.resolve(fileName)
        val timestamp = now.format(timestampFormatter)
        val line      = "$timestamp | $message${System.lineSeparator()}"

        logger.info { message }

        scope.launch {
            try {
                if (!Files.exists(logFile)) {
                    Files.createFile(logFile)
                }
                Files.write(
                    logFile,
                    line.toByteArray(),
                    StandardOpenOption.APPEND
                )
            } catch (e: IOException) {
                logger.error(e) { "Ошибка записи в файл логов: $logFile" }
            }
        }
    }

    /** Вызывать при завершении приложения, чтобы отменить фоновые корутины. */
    fun shutdown() {
        scope.cancel("Logger shutdown")
    }
}
