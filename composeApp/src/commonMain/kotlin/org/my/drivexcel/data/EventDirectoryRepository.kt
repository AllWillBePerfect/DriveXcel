package org.my.drivexcel.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.xmlbeans.impl.common.XMLChar.isValidName
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.DirectoryPathProvider
import org.my.drivexcel.platform.utils.LeaderUser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

interface EventDirectoryRepository {


    fun getEventsFlow(): Flow<List<EventDirData>>
    fun getEventFlow(id: String): Flow<EventDirData?>
    suspend fun getEvent(id: String): EventDirData?

    fun getExcelDataFlow(id: String): Flow<ExcelData?>

    suspend fun createEventDir(eventData: EventData): AppResult<Unit, EventDirErrors>
    suspend fun changeEventDir(
        id: String,
        eventData: EventData
    ): AppResult<Unit, EventDirErrors>

    suspend fun deleteEventDir(id: String): AppResult<Unit, EventDirErrors>


    class Impl(
        private val fileDataSource: FileDataSource,
        private val metaDataSource: MetaDataSource,
        private val imageDataSource: ImageDataSource,
        private val excelDataSource: ExcelDataSource,
        private val appLogger: AppLogger,
    ) : EventDirectoryRepository {

        /*private fun getSubDirsFlow(root: File): Flow<List<File>> = callbackFlow {
            println("SubDirs + internal file watcher started")

            val watchService = FileSystems.getDefault().newWatchService()
            val registeredDirs = mutableSetOf<Path>()

            fun registerDirRecursively(dir: File) {
                if (!dir.exists() || !dir.isDirectory) return
                val path = dir.toPath()
                if (registeredDirs.add(path)) {
                    path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    )
                    dir.listFiles()?.filter { it.isDirectory }?.forEach {
                        registerDirRecursively(it)
                    }
                }
            }

            // Регистрируем корневую директорию и все поддиректории
            registerDirRecursively(root)

            // Эмит начального состояния — список директорий
            trySend(root.walkTopDown().filter { it.isDirectory }.toList())

            val job = launch(Dispatchers.IO) {
                try {
                    while (isActive) {
                        val key = watchService.take()
                        val dir = key.watchable() as Path

                        key.pollEvents().forEach { event ->
                            val kind = event.kind()
                            val contextPath = dir.resolve(event.context() as Path)
                            val file = contextPath.toFile()

                            if (kind == StandardWatchEventKinds.ENTRY_CREATE && file.isDirectory) {
                                // Если создали новую поддиректорию — регистрируем её рекурсивно
                                registerDirRecursively(file)
                            }
                        }

                        // Эмит обновленного списка директорий при любом событии
                        val dirs = root.walkTopDown().filter { it.isDirectory }.toList()
                        trySend(dirs)

                        if (!key.reset()) {
                            registeredDirs.remove(dir)
                        }
                    }
                } catch (e: ClosedWatchServiceException) {
                    println("WatchService closed")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            awaitClose {
                println("SubDirs + internal file watcher closed")
                job.cancel()
                watchService.close()
            }
        }.distinctUntilChanged()
            .conflate()
            .flowOn(Dispatchers.IO)*/


        private fun getSubDirsFlow(root: File): Flow<List<File>> = callbackFlow {
            println("SubDirs callbackFlow started")

            trySend(root.listFiles()?.filter { it.isDirectory } ?: emptyList())

            val watchService = FileSystems.getDefault().newWatchService()
            val path = root.toPath()

            path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )

            val job = launch(Dispatchers.IO) {
                try {
                    while (isActive) {
                        val key = watchService.take()
                        val dirs = root.listFiles()?.filter { it.isDirectory } ?: emptyList()
                        trySend(dirs)
                        if (!key.reset()) break
                    }
                } catch (e: ClosedWatchServiceException) {
                    println("WatchService closed, stopping watcher")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            awaitClose {
                println("SubDirs callbackFlow closed")
                job.cancel()
                watchService.close()
            }
        }.distinctUntilChanged()
            .conflate()
            .flowOn(Dispatchers.IO)

        override fun getEventsFlow(): Flow<List<EventDirData>> =
            getSubDirsFlow(fileDataSource.eventsRoot)
                .map { dirs ->
                    dirs.mapNotNull { eventDir ->
                        val meta = metaDataSource.readMeta(eventDir)
                        if (meta == null) {
                            appLogger.e(
                                "EventDirectoryRepository",
                                "По этому пути не получилось прочитать meta файл: ${eventDir.absolutePath}"
                            )
                            return@mapNotNull null
                        }

                        val image = eventDir.listFiles()?.find { file ->
                            !file.isDirectory && file.name.lowercase().startsWith("preview")
                        }

                        EventDirData(
                            id = meta.id,
                            name = meta.name,
                            image = image,

                            )
                    }
                }
                .distinctUntilChanged()
                .conflate()


        /*  override fun getEventFlow(id: String): Flow<EventDirData?> =
              getEventsFlow().map { list -> list.find { it.id == id } }.flowOn(Dispatchers.IO)
  */

        /*override fun getEventFlow(id: String): Flow<EventDirData?> =
            getSubDirsFlow(fileDataSource.eventsRoot)
                .map { dirs ->
                    dirs.firstNotNullOfOrNull { dir ->
                        val meta = metaDataSource.readMeta(dir)
                        if (meta?.id == id) EventDirData(meta.id, meta.name, dir) else null
                    }
                }*/

        override fun getEventFlow(id: String): Flow<EventDirData?> =
            getEventsFlow().map { events ->
                events.firstOrNull { eventDirData ->
                    eventDirData.id == id
                }
            }


        override suspend fun getEvent(id: String): EventDirData? {
            return withContext(Dispatchers.IO) {
                getAllEvents().find { it.id == id }
            }
        }

        /*override suspend fun getExcelDataFlow(id: String): Flow<ExcelData?> {
            return getEventsFlow().map {

                val eventDir = fileDataSource
                    .listSubDirs(fileDataSource.eventsRoot)
                    .find { dir ->
                        metaDataSource.readMeta(dir)?.id == id
                    } ?: return@map null

                val excelFile = eventDir
                    .listFiles()
                    ?.find { it.name.endsWith(".xls") || it.name.endsWith(".xlsx") }
                    ?: return@map null

                excelDataSource.readExcel(excelFile)

            }.catch { e ->
                println("Ошибка при readExcel: $e")
                emit(null)
            }
        }*/

        override fun getExcelDataFlow(id: String): Flow<ExcelData?> =
            getSubDirsFlow(fileDataSource.eventsRoot)
                .map { dirs ->
                    val eventDir =
                        dirs.find { metaDataSource.readMeta(it)?.id == id } ?: return@map null
                    val excelFile = eventDir.listFiles()
                        ?.find { it.name.endsWith(".xls") || it.name.endsWith(".xlsx") }
                        ?: return@map null

                    withContext(Dispatchers.IO) {
                        excelDataSource.readExcel(excelFile)
                    }
                }.catch {
                    println("Ошибка при readExcel: $it")
                    emit(null)
                }


        @OptIn(ExperimentalTime::class)
        override suspend fun createEventDir(eventData: EventData): AppResult<Unit, EventDirErrors> {
            return withContext(Dispatchers.IO) {

                if (!isValidName(eventData.name)) return@withContext AppResult.Error(
                    EventDirErrors.InvalidName
                )

                val existing = fileDataSource.listSubDirs(fileDataSource.eventsRoot)
                    .mapNotNull { metaDataSource.readMeta(it) }.any {
                        it.name.equals(eventData.name, true)
                    }

                if (existing) return@withContext AppResult.Error(
                    EventDirErrors.AlreadyExists
                )

                try {
                    val id = UUID.randomUUID().toString()
                    val eventDir = File(fileDataSource.eventsRoot, id)

                    val meta = EventDirectoryMeta(
                        id = id,
                        name = eventData.name,
                        createdAt = Clock.System.now()
                            .toJavaInstant()
                            .atOffset(ZoneOffset.UTC)
                            .format(DateTimeFormatter.ISO_INSTANT)
                    )

                    fileDataSource.createDir(eventDir)
                    metaDataSource.updateMeta(eventDir, meta)
                    eventData.image?.let { bytes -> imageDataSource.addImage(eventDir, bytes) }

                    return@withContext AppResult.Success(Unit)
                } catch (e: Exception) {
                    return@withContext AppResult.Error(EventDirErrors.Unhandled(e))
                }
            }
        }

        @OptIn(ExperimentalTime::class)
        override suspend fun changeEventDir(
            id: String,
            eventData: EventData
        ): AppResult<Unit, EventDirErrors> {
            return withContext(Dispatchers.IO) {

                if (!isValidName(eventData.name)) return@withContext AppResult.Error(
                    EventDirErrors.InvalidName
                )

                val existing = fileDataSource.listSubDirs(fileDataSource.eventsRoot)
                    .mapNotNull { metaDataSource.readMeta(it) }.any {
                        it.name.equals(eventData.name, true) && it.id != id
                    }

                if (existing) return@withContext AppResult.Error(
                    EventDirErrors.AlreadyExists
                )

                val eventDir = findDirById(id) ?: return@withContext AppResult.Error(
                    EventDirErrors.NotFoundDir
                )

                val oldMeta = metaDataSource.readMeta(eventDir)
                    ?: return@withContext AppResult.Error(EventDirErrors.CorruptedMeta)


                try {
                    val meta = oldMeta.copy(
                        name = eventData.name
                    )

                    metaDataSource.updateMeta(eventDir, meta)
                    eventData.image?.let { bytes -> imageDataSource.addImage(eventDir, bytes) }
                        ?: imageDataSource.deleteImage(eventDir)

                    return@withContext AppResult.Success(Unit)
                } catch (e: Exception) {
                    return@withContext AppResult.Error(EventDirErrors.Unhandled(e))

                }
            }
        }

        override suspend fun deleteEventDir(id: String): AppResult<Unit, EventDirErrors> {
            return withContext(Dispatchers.IO) {
                val eventDir = findDirById(id)
                    ?: return@withContext AppResult.Error(EventDirErrors.NotFoundDir)

                try {
                    fileDataSource.deleteDir(eventDir)
                    return@withContext AppResult.Success(Unit)
                } catch (e: Exception) {
                    return@withContext AppResult.Error(
                        EventDirErrors.Unhandled(e)
                    )
                }
            }
        }

        private fun getAllEvents(): List<EventDirData> =
            fileDataSource.listSubDirs(fileDataSource.eventsRoot).mapNotNull { eventDir ->
                val meta = metaDataSource.readMeta(eventDir)
                if (meta == null) {
                    appLogger.e(
                        "EventDirectoryRepository",
                        "По этому пути не получилось прочитать meta файл: ${eventDir.absolutePath}"
                    )
                    return@mapNotNull null
                }

                val image = eventDir.listFiles().find { file ->
                    !file.isDirectory && file.name.lowercase().startsWith("preview")
                }

                EventDirData(
                    id = meta.id,
                    name = meta.name,
                    image = image
                )

            }


        private fun findDirById(id: String): File? =
            fileDataSource.listSubDirs(fileDataSource.eventsRoot)
                .firstOrNull { metaDataSource.readMeta(it)?.id == id }
    }
}

interface FileDataSource {

    val eventsRoot: File

    fun createDir(file: File)
    fun renameDir(from: File, to: File)
    fun deleteDir(file: File)

    fun listSubDirs(dir: File): List<File>

    class Impl(
        private val directoryPathProvider: DirectoryPathProvider
    ) : FileDataSource {

        /* override val eventsRoot: File by lazy {
             val homeDir = directoryPathProvider.provideHomePathFile().apply { createDir(this) }
             File(
                 homeDir,
                 AppLogger.LOCAL_ROOT_DIRECTORY
             ).apply { createDir(this) }
         }*/

        override val eventsRoot: File by lazy {
            File(
                directoryPathProvider.provideHomePathFile(),
                AppLogger.LOCAL_ROOT_DIRECTORY
            ).apply { createDir(this) }
        }

        override fun createDir(file: File) {
            if (!file.exists()) file.mkdirs()
        }

        override fun renameDir(from: File, to: File) {
            from.renameTo(to)
        }

        override fun deleteDir(file: File) {
            if (file.exists())
                file.deleteRecursively()
        }

        override fun listSubDirs(dir: File): List<File> =
            dir.listFiles()?.filter { it.isDirectory } ?: emptyList()

    }
}

interface ImageDataSource {

    fun addImage(eventDir: File, bytes: ByteArray)
    fun deleteImage(eventDir: File)

    class Impl() : ImageDataSource {

        override fun addImage(eventDir: File, bytes: ByteArray) {
            val ext = guessImageExtension(bytes)
            val f = File(eventDir, "preview.$ext")
            FileOutputStream(f).use { it.write(bytes) }
        }

        override fun deleteImage(eventDir: File) {
            eventDir.listFiles()
                .filter { it.isFile && it.name.lowercase().startsWith("preview") }
                .forEach { it.delete() }
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

//            private val imageTypes = listOf("preview.png", "preview.jpg", "preview.webp")
    }

}

interface MetaDataSource {

    fun updateMeta(eventDir: File, meta: EventDirectoryMeta)
    fun readMeta(eventDir: File): EventDirectoryMeta?

    class Impl() : MetaDataSource {

        override fun updateMeta(eventDir: File, meta: EventDirectoryMeta) {
            val stringMeta = Json.encodeToString(EventDirectoryMeta.serializer(), meta)
            val metaFile = File(eventDir, "meta.json")
            metaFile.writeText(stringMeta)
        }

        override fun readMeta(eventDir: File): EventDirectoryMeta? {
            val metaFile = eventDir.listFiles().find { it.name.lowercase() == "meta.json" }
            val metaString = metaFile?.readText()
            return metaString?.let {
                Json.decodeFromString(
                    EventDirectoryMeta.serializer(),
                    metaString
                )
            }
        }

    }
}

interface ExcelDataSource {
    fun readExcel(excelDir: File): ExcelData

    class Impl() : ExcelDataSource {
        override fun readExcel(excelDir: File): ExcelData {
            val users = mutableListOf<LeaderUser>()

            FileInputStream(excelDir).use { inputStream ->
                XSSFWorkbook(inputStream).use { workbook ->

                    val sheet = workbook.getSheetAt(0)
                    val rows = sheet.drop(1)

                    for (row in rows) {
                        fun getCellString(index: Int): String? {
                            val cell = row.getCell(index)
                            return when (cell?.cellType) {
                                CellType.STRING -> cell.stringCellValue.trim()
                                CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                                else -> null
                            }
                        }

                        val user = LeaderUser(
                            id = getCellString(0)?.toIntOrNull() ?: 0,
                            fullName = getCellString(1) ?: "",
                            age = getCellString(2)?.toIntOrNull() ?: 0,
                            company = getCellString(3),
                            jobTitle = getCellString(4),
                            role = getCellString(5) ?: "",
                            format = getCellString(6) ?: "",
                            blackMark = getCellString(7)?.equals(
                                "true",
                                ignoreCase = true
                            ) == true,
                            dateOfVisit = getCellString(8),
                            applicationDate = getCellString(9) ?: "",
                            applicationStatus = getCellString(10) ?: "",
                            email = getCellString(11) ?: "",
                            phone = getCellString(12) ?: "",
                            city = getCellString(13) ?: "",
                            region = getCellString(14) ?: "",
                            placeOfStudy = getCellString(15) ?: "",
                            speciality = getCellString(16),
                            formOfStudy = getCellString(17),
                            studyFormat = getCellString(18),
                            educationLevel = getCellString(19)
                        )
                        users.add(user)
                    }


                }
            }

            return ExcelData(
                users = users
            )
        }
    }
}

data class ExcelData(
    val users: List<LeaderUser>
)

/*sealed interface CreateEventDirErrors {
    object InvalidName : CreateEventDirErrors
    object AlreadyExists : CreateEventDirErrors
    data class Unhandled(val exception: Exception) : CreateEventDirErrors
}

sealed interface UpdateEventDirErrors {
    object InvalidName : UpdateEventDirErrors
    object AlreadyExists : UpdateEventDirErrors
    object NotFoundDir : UpdateEventDirErrors
    object CorruptedMeta : UpdateEventDirErrors
    data class Unhandled(val exception: Exception) : UpdateEventDirErrors
}

sealed interface DeleteEventDirErrors {
    object NotFoundDir : DeleteEventDirErrors
    data class Unhandled(val exception: Exception) : DeleteEventDirErrors
}*/

sealed interface EventDirErrors {
    object InvalidName : EventDirErrors
    object AlreadyExists : EventDirErrors
    object NotFoundDir : EventDirErrors
    object CorruptedMeta : EventDirErrors
    data class Unhandled(val exception: Exception) : EventDirErrors
}

data class EventDirData(
    val id: String,
    val name: String,
    val image: File?
)

sealed class AppResult<out T, out E> {
    data class Success<T>(val data: T) : AppResult<T, Nothing>()
    data class Error<E>(val error: E) : AppResult<Nothing, E>()
}

inline fun <T, E, R> AppResult<T, E>.map(transform: (T) -> R): AppResult<R, E> =
    when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Error -> this
    }

inline fun <T, E, R> AppResult<T, E>.mapError(transform: (E) -> R): AppResult<T, R> =
    when (this) {
        is AppResult.Success -> this
        is AppResult.Error -> AppResult.Error(transform(error))
    }

inline fun <T, E> AppResult<T, E>.onSuccess(action: (T) -> Unit): AppResult<T, E> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T, E> AppResult<T, E>.onFailure(action: (E) -> Unit): AppResult<T, E> {
    if (this is AppResult.Error) action(error)
    return this
}

inline fun <T, E, R> AppResult<T, E>.fold(
    onSuccess: (T) -> R,
    onError: (E) -> R
): R = when (this) {
    is AppResult.Success -> onSuccess(data)
    is AppResult.Error -> onError(error)
}

