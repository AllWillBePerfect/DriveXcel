package org.my.drivexcel.data

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.xmlbeans.impl.common.XMLChar.isValidName
import org.my.drivexcel.platform.datasources.EventDirectory
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.DirectoryPathProvider
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

interface EventDirectoryManager {

    val events: Flow<List<EventDirectory>>

    suspend fun createEventDir(newData: EventData): EventOpResult
    suspend fun deleteEventDir(name: String): EventOpResult
    suspend fun redactingEventDir(id: String, newData: EventData): EventOpResult

    suspend fun getAllEvents(): List<EventDirectory>
    suspend fun getEvent(id: String): EventDirectory?

    class Impl(
        private val fileUtils: FileUtils,
        private val directoryPathProvider: DirectoryPathProvider
    ) : EventDirectoryManager {

        private val eventsRoot: File by lazy {
            val homeDir = directoryPathProvider.provideHomePathFile().apply { mkdirsSafe() }
            File(
                homeDir,
                AppLogger.JVM_LOCAL_ROOT_DIRECTORY
            ).apply { mkdirsSafe() }
        }

        override val events: Flow<List<EventDirectory>> = callbackFlow {
            trySend(getAllEvents())

            val watchService = FileSystems.getDefault().newWatchService()
            val path = eventsRoot.toPath()

            path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )

            val job = launch(Dispatchers.IO) {
                while (isActive) {
                    val key = watchService.take()
                    trySend(getAllEvents())
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
        override suspend fun createEventDir(newData: EventData): EventOpResult =
            withContext(Dispatchers.IO) {
                if (!isValidName(newData.name)) return@withContext EventOpResult.InvalidName

                val existing = getAllEvents()
                    .any { it.id == newData.name || readMeta(it.id)?.name == newData.name }

                if (existing) return@withContext EventOpResult.AlreadyExists

                val id = UUID.randomUUID().toString()
                val newDir = File(eventsRoot, id)

                var tmpMetaFile: File? = null
                var tmpImageFile: File? = null

                try {
                    fileUtils.ensureDir(newDir)

                    val meta = EventDirectoryMeta(
                        id = id,
                        name = newData.name,
                        createdAt = Clock.System.now()
                            .toJavaInstant()
                            .atOffset(ZoneOffset.UTC)
                            .format(DateTimeFormatter.ISO_INSTANT)
                    )

                    // --- tmp meta ---
                    tmpMetaFile = File(newDir, "meta.json.tmp")
                    fileUtils.writeText(
                        tmpMetaFile,
                        Json.encodeToString(EventDirectoryMeta.serializer(), meta)
                    )

                    // --- tmp image ---
                    tmpImageFile = newData.image?.let { bytes ->
                        val ext = guessImageExtension(bytes)
                        val f = File(newDir, "preview.$ext.tmp")
                        fileUtils.writeBytes(f, bytes)
                        f
                    }

                        //TODO переделать
                        .also { imageTypes.forEach { fileUtils.deleteFile(File(newDir, it)) } }



                    // --- атомарная замена ---
                    val metaFile = File(newDir, "meta.json")
                    if (!fileUtils.renameDir(
                            tmpMetaFile,
                            metaFile
                        )
                    ) throw IOException("Failed to replace meta.json")
                    tmpMetaFile = null

                    tmpImageFile?.let { tmp ->
                        val finalPreview = File(newDir, tmp.name.removeSuffix(".tmp"))
                        if (finalPreview.exists()) finalPreview.delete()
                        if (!fileUtils.renameDir(
                                tmp,
                                finalPreview
                            )
                        ) throw IOException("Failed to replace preview image")
                        tmpImageFile = null
                    }

                    EventOpResult.Success

                } catch (e: Exception) {
                    EventOpResult.IOError(e)
                } finally {
                    tmpMetaFile?.delete()
                    tmpImageFile?.delete()
                }
            }


        override suspend fun deleteEventDir(name: String): EventOpResult =
            withContext(Dispatchers.IO) {
                val dir = File(eventsRoot, name)

                if (!dir.exists()) return@withContext EventOpResult.NotFound

                /*// ❌ в директории есть файлы → запрещаем удаление?
                if (dir.listFiles()?.isNotEmpty() == true)
                    return@withContext EventOpResult.HasFiles*/

                return@withContext if (fileUtils.deleteDir(dir))
                    EventOpResult.Success
                else
                    EventOpResult.IOError(IOException("Delete failed"))
            }


        override suspend fun redactingEventDir(id: String, newData: EventData): EventOpResult =
            withContext(Dispatchers.IO) {
                if (!isValidName(newData.name)) return@withContext EventOpResult.InvalidName

                val dir = File(eventsRoot, id)
                if (!dir.exists()) return@withContext EventOpResult.NotFound

                val oldMeta = readMeta(id) ?: return@withContext EventOpResult.NotFound

                var tmpMetaFile: File? = null
                var tmpImageFile: File? = null

                try {
                    // --- tmp meta ---
                    val newMeta = oldMeta.copy(name = newData.name)
                    tmpMetaFile = File(dir, "meta.json.tmp")
                    fileUtils.writeText(
                        tmpMetaFile,
                        Json.encodeToString(EventDirectoryMeta.serializer(), newMeta)
                    )

                    // --- tmp image ---
                    tmpImageFile = newData.image?.let { imageBytes ->
                        val ext = guessImageExtension(imageBytes)
                        val f = File(dir, "preview.$ext.tmp")
                        fileUtils.writeBytes(f, imageBytes)
                        f
                    }  //TODO переделать
                        .also { imageTypes.forEach { fileUtils.deleteFile(File(dir, it)) } }


                    // --- атомарная замена ---
                    val metaFile = File(dir, "meta.json")
                    if (!fileUtils.renameDir(
                            tmpMetaFile,
                            metaFile
                        )
                    ) throw IOException("Failed to replace meta.json")
                    tmpMetaFile = null

                    tmpImageFile?.let { tmp ->
                        val finalPreview = File(dir, tmp.name.removeSuffix(".tmp"))
                        if (finalPreview.exists()) finalPreview.delete()
                        if (!fileUtils.renameDir(
                                tmp,
                                finalPreview
                            )
                        ) throw IOException("Failed to replace preview image")
                        tmpImageFile = null
                    }

                    EventOpResult.Success
                } catch (e: Exception) {
                    EventOpResult.IOError(e)
                } finally {
                    tmpMetaFile?.delete()
                    tmpImageFile?.delete()
                }
            }


        override suspend fun getAllEvents(): List<EventDirectory> =
            withContext(Dispatchers.IO) {
                fileUtils.listSubDirs(eventsRoot).mapNotNull { dir ->
                    val meta = readMeta(dir.name) ?: return@mapNotNull null

                    val preview = getImage(dir)

                    EventDirectory(
                        id = meta.id,
                        name = meta.name,
                        imagePath = preview
                    )
                }
            }


        override suspend fun getEvent(id: String): EventDirectory? =
            withContext(Dispatchers.IO) {
                val dir = File(eventsRoot, id)
                val meta = readMeta(id) ?: return@withContext null

                val preview = getImage(dir)

                EventDirectory(
                    id = meta.id,
                    name = meta.name,
                    imagePath = preview
                )
            }

        private fun getImage(dir: File) = imageTypes
            .map { File(dir, it) }
            .firstOrNull { it.exists() }
            ?.absolutePath

        private fun File.mkdirsSafe() {
            try {
                fileUtils.ensureDir(this)
            } catch (e: Exception) {
                println("⚠️ Failed to create directory: $absolutePath")
            }
        }

        private fun readMeta(eventDirName: String): EventDirectoryMeta? {
            val dir = File(eventsRoot, eventDirName)
            if (!dir.exists() || !dir.isDirectory) return null

            val metaFile = File(dir, "meta.json")
            if (!metaFile.exists()) return null

            return try {
                fileUtils.readText(metaFile)
                    ?.let { Json.decodeFromString(EventDirectoryMeta.serializer(), it) }
            } catch (e: Exception) {
                // Можно логировать, но не бросаем исключение — повреждённый meta не должен ронять приложение
                null
            }
        }

        private fun guessImageExtension(bytes: ByteArray): String {
            return when {
                bytes.size >= 4 &&
                        bytes[0] == 0x89.toByte() &&
                        bytes[1] == 0x50.toByte() && // 'P'
                        bytes[2] == 0x4E.toByte() && // 'N'
                        bytes[3] == 0x47.toByte()    // 'G'
                    -> "png"

                bytes.size >= 3 &&
                        bytes[0] == 0xFF.toByte() &&
                        bytes[1] == 0xD8.toByte() &&
                        bytes[2] == 0xFF.toByte()
                    -> "jpg"

                bytes.size >= 12 && // WEBP (RIFF header + "WEBP")
                        bytes[0] == 'R'.code.toByte() &&
                        bytes[1] == 'I'.code.toByte() &&
                        bytes[2] == 'F'.code.toByte() &&
                        bytes[3] == 'F'.code.toByte() &&
                        bytes[8] == 'W'.code.toByte() &&
                        bytes[9] == 'E'.code.toByte() &&
                        bytes[10] == 'B'.code.toByte() &&
                        bytes[11] == 'P'.code.toByte()
                    -> "webp"

                else -> "bin" // если формат неизвестен — не ломаемся
            }
        }

        val imageTypes = listOf("preview.png", "preview.jpg", "preview.webp")
    }
}

sealed class EventOpResult {
    object Success : EventOpResult()

    object NotFound : EventOpResult()
    object AlreadyExists : EventOpResult()
    object InvalidName : EventOpResult()
    object HasFiles : EventOpResult()

    data class IOError(val error: Throwable) : EventOpResult()
}


data class EventData(
    val name: String,
    val image: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventData

        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}

@Serializable
data class EventDirectoryMeta(
    val id: String,
    val name: String,
    val createdAt: String
)