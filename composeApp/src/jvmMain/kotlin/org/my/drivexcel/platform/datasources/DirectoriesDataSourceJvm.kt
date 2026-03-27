package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.my.drivexcel.platform.utils.AppLogger
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

/*
class DirectoriesDataSourceJvm(
    private val appLogger: AppLogger
) : DirectoriesDataSource {

    private val eventsSubDir: File by lazy {
        val eventDir = Paths.get(
            System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
            AppLogger.JVM_FOLDER_DIRECTORY_NAME
        ).toFile()
        eventDir.mkdirsSafe()

        File(eventDir, AppLogger.JVM_EVENTS_ROOT_DIRECTORY).apply {
            mkdirsSafe()
        }
    }

    */
/*init {
        val eventDir =
            Paths.get(
                System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
                AppLogger.JVM_FOLDER_DIRECTORY_NAME
            ).toFile()
        if (!eventDir.exists()) {
            eventDir.mkdirs()
        }

        eventsSubDir = File(eventDir, AppLogger.JVM_EVENTS_ROOT_DIRECTORY).apply {
            if (!exists()) mkdirs()
        }
    }*//*



    override val events: Flow<List<EventDirectory>> = callbackFlow {
        trySend(readAllEventDirectories())

        val watchService = FileSystems.getDefault().newWatchService()
        val path = eventsSubDir.toPath()
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )

        val job = launch(Dispatchers.IO) {
            while (isActive) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    val kind = event.kind()
                    if (kind == StandardWatchEventKinds.OVERFLOW) continue

                    trySend(readAllEventDirectories())
                }
                if (!key.reset()) break
            }
        }

        awaitClose {
            job.cancel()
            watchService.close()
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(Dispatchers.IO)


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

        val utcNow = Clock.System.now().toJavaInstant()
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

                val imagePath = previewFile?.absolutePath

                EventDirectory(eventName = eventName, imagePath = imagePath)
            } catch (_: Exception) {
                null
            }
        } ?: emptyList()
    }

    */
/**
     * 🔍 Ищет первую директорию, содержащую .xls или .xlsx файл,
     * и возвращает путь к найденному файлу.
     * Если ничего не найдено — возвращает null.
     *//*

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

    */
/**
     * 🔍 Ищет первый Excel-файл (.xls или .xlsx) в директории по имени события.
     * @param eventName имя папки (например, "Event_2025_10_25")
     * @return абсолютный путь к Excel-файлу или null, если не найден.
     *//*

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

    */
/** Безопасное создание директории с логированием *//*

    private fun File.mkdirsSafe() {
        if (!exists()) {
            if (!mkdirs()) appLogger.d("mkdirsSafe","⚠️ Failed to create directory: $absolutePath")
        }
    }

}
*/

class DirectoriesDataSourceJvm : DirectoriesDataSource {

    private val eventsSubDir: File by lazy {
        val eventDir = Paths.get(
            System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
            AppLogger.JVM_FOLDER_DIRECTORY_NAME
        ).toFile()
        eventDir.mkdirsSafe()

        File(eventDir, AppLogger.LOCAL_ROOT_DIRECTORY).apply {
            mkdirsSafe()
        }
    }

    override val events: Flow<List<EventDirectory>> = callbackFlow {
        trySend(readAllEventDirectories())

        val watchService = FileSystems.getDefault().newWatchService()
        val path = eventsSubDir.toPath()
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )

        val job = launch(Dispatchers.IO) {
            while (isActive) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue
                    trySend(readAllEventDirectories())
                }
                if (!key.reset()) break
            }
        }

        awaitClose {
            job.cancel()
            watchService.close()
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    override suspend fun createEventDirectory(eventName: String, imageBytes: ByteArray?) = withContext(Dispatchers.IO) {
        eventsSubDir.mkdirsSafe()

        val eventDir = File(eventsSubDir, eventName)
        if (eventDir.exists()) return@withContext
        eventDir.mkdirsSafe()

        val imageFileName = "preview.png"
        if (imageBytes != null) {
            val imageFile = File(eventDir, imageFileName)
            try {
                FileOutputStream(imageFile).use { out -> out.write(imageBytes) }
            } catch (e: IOException) {
                println("❌ Failed to save image: ${e.message}")
            }
        }

        val utcNow = Clock.System.now().toJavaInstant()
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

    private suspend fun readAllEventDirectories(): List<EventDirectory> = withContext(Dispatchers.IO) {
        eventsSubDir.listFiles()?.mapNotNull { dir ->
            val metaFile = File(dir, "meta.json")
            if (!metaFile.exists()) return@mapNotNull null

            try {
                val json = metaFile.readText()
                val element = kotlinx.serialization.json.Json.parseToJsonElement(json)
                val eventName = element.jsonObject["eventName"]?.jsonPrimitive?.content ?: return@mapNotNull null

                val previewFile = listOf(
                    File(dir, "preview.jpg"),
                    File(dir, "preview.png")
                ).firstOrNull { it.exists() }

                EventDirectory(id = eventName, name = "", imagePath = previewFile?.absolutePath)
            } catch (e: Exception) {
                println("❌ Failed to read event directory: ${e.message}")
                null
            }
        } ?: emptyList()
    }

    override suspend fun findFirstExcelFilePath(): String? = withContext(Dispatchers.IO) {
        eventsSubDir.listFiles()?.forEach { dir ->
            if (!dir.isDirectory) return@forEach
            dir.listFiles()?.firstOrNull { it.name.lowercase().endsWith(".xls") || it.name.lowercase().endsWith(".xlsx") }?.let {
                return@withContext it.absolutePath
            }
        }
        null
    }

    override suspend fun findExcelFilePathByEventName(eventName: String): String? = withContext(Dispatchers.IO) {
        val targetDir = File(eventsSubDir, eventName)
        if (!targetDir.exists() || !targetDir.isDirectory) return@withContext null

        targetDir.listFiles()?.firstOrNull { it.name.lowercase().endsWith(".xls") || it.name.lowercase().endsWith(".xlsx") }?.absolutePath
    }

    /** Безопасное создание директории с логированием */
    private fun File.mkdirsSafe() {
        if (!exists()) {
            if (!mkdirs()) println("⚠️ Failed to create directory: $absolutePath")
        }
    }

}