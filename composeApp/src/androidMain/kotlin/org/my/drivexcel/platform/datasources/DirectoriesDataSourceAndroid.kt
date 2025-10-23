package org.my.drivexcel.platform.datasources


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

//FIXME make correct implementation

class DirectoriesDataSourceAndroid(
    private val context: Context
) : DirectoriesDataSource {

    private val eventsSubDir: File

    init {
        val eventDir = File(context.filesDir, "DriveXcel")
        if (!eventDir.exists()) eventDir.mkdirs()

        eventsSubDir = File(eventDir, "events").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Поток всех событий. На Android нет WatchService, поэтому
     * проверяем содержимое директории каждые 1-2 секунды.
     */
    override val events: Flow<List<EventDirectory>> = flow {
        var lastEvents = emptyList<EventDirectory>()
        while (true) {
            val currentEvents = readAllEventDirectories()
            if (currentEvents != lastEvents) {
                emit(currentEvents)
                lastEvents = currentEvents
            }
            delay(1000L)
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    override fun createEventDirectory(eventName: String, imageBytes: ByteArray?) {
        val eventDir = File(eventsSubDir, eventName)
        if (eventDir.exists()) return
        eventDir.mkdirs()

        val imageFileName = "preview.png"
        val imageFile = File(eventDir, imageFileName)

        if (imageBytes != null) {
            try {
                FileOutputStream(imageFile).use { out ->
                    out.write(imageBytes)
                }
            } catch (e: IOException) {
                println("❌ Failed to save image: ${e.message}")
            }
        }

        val utcNow = Clock.System.now()
            .toJavaInstant()
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)

        val metaFile = File(eventDir, "meta.json")
        val metaJson = buildJsonObject {
            put("eventName", eventName)
            put("imageFile", if (imageBytes != null) imageFileName else null)
            put("createdAtUtc0", utcNow)
        }.toString()

        metaFile.writeText(metaJson)
    }

    private fun readAllEventNames(): List<String> {
        return eventsSubDir.listFiles()?.mapNotNull { dir ->
            val metaFile = File(dir, "meta.json")
            if (metaFile.exists()) {
                try {
                    val json = metaFile.readText()
                    val element = kotlinx.serialization.json.Json.parseToJsonElement(json)
                    element.jsonObject["eventName"]?.jsonPrimitive?.content
                } catch (_: Exception) {
                    null
                }
            } else null
        } ?: emptyList()
    }

    private fun readAllEventDirectories(): List<EventDirectory> {
        return eventsSubDir.listFiles()?.mapNotNull { dir ->
            val metaFile = File(dir, "meta.json")
            if (!metaFile.exists()) return@mapNotNull null

            try {
                val json = metaFile.readText()
                val element = kotlinx.serialization.json.Json.parseToJsonElement(json)
                val eventName = element.jsonObject["eventName"]?.jsonPrimitive?.content ?: return@mapNotNull null

                // Ищем preview.jpg или preview.png
                val previewFile = listOf(
                    File(dir, "preview.jpg"),
                    File(dir, "preview.png")
                ).firstOrNull { it.exists() }

                val imageBytes = previewFile?.readBytes()

                EventDirectory(eventName = eventName, image = imageBytes)
            } catch (_: Exception) {
                null
            }
        } ?: emptyList()
    }

    /**
     * 🔍 Ищет первую директорию, содержащую .xls или .xlsx файл,
     * и возвращает путь к найденному файлу.
     * Если ничего не найдено — возвращает null.
     */
    override fun findFirstExcelFilePath(): String? {
        val dirs = eventsSubDir.listFiles() ?: return null

        for (dir in dirs) {
            if (dir.isDirectory) {
                val excelFile = dir.listFiles()?.firstOrNull { file ->
                    val name = file.name.lowercase()
                    name.endsWith(".xls") || name.endsWith(".xlsx")
                }
                if (excelFile != null) {
                    return excelFile.absolutePath
                }
            }
        }

        return null
    }

    /**
     * 🔍 Ищет первый Excel-файл (.xls или .xlsx) в директории по имени события.
     * @param eventName имя папки (например, "Event_2025_10_25")
     * @return абсолютный путь к Excel-файлу или null, если не найден.
     */
    override fun findExcelFilePathByEventName(eventName: String): String? {
        val targetDir = File(eventsSubDir, eventName)
        if (!targetDir.exists() || !targetDir.isDirectory) {
            println("⚠️ Папка '$eventName' не найдена в ${eventsSubDir.absolutePath}")
            return null
        }

        val excelFile = targetDir.listFiles()?.firstOrNull { file ->
            val name = file.name.lowercase()
            name.endsWith(".xls") || name.endsWith(".xlsx")
        }

        return excelFile?.absolutePath
    }
}
