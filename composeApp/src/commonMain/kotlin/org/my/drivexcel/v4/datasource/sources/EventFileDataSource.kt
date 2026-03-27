package org.my.drivexcel.v4.datasource.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.my.drivexcel.data.v3.FolderIdProvider
import org.my.drivexcel.domain.models.EventImage
import org.my.drivexcel.v4.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.v4.data.SerializableParser
import org.my.drivexcel.v4.data.models.HistoryDataModel
import org.my.drivexcel.v4.data.models.HistoryEntry
import org.my.drivexcel.v4.data.models.LeaderUserDataModel
import org.my.drivexcel.v4.data.models.MetaDataModel
import org.my.drivexcel.v4.data.models.UsersDataModel
import org.my.drivexcel.v4.data.models.buildHistoryActions
import org.my.drivexcel.v4.domain.model.EventDomainModel
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

interface EventFileDataSource {

    suspend fun createEvent(eventName: String, image: EventImage?): String
    suspend fun updateEvent(dirId: String, eventName: String, image: EventImage?)
    suspend fun deleteEvent(dirId: String)

    suspend fun getEvent(id: String): EventDomainModel
    suspend fun getEvents(): List<EventDomainModel>
    fun getEventsFlow(): Flow<List<EventDomainModel>>

    suspend fun mergeXls(dirId: String, list: List<ByteArray>)
    suspend fun getLeaderUsers(dirId: String): List<LeaderUserDataModel>
    fun getLeaderUsersFlow(dirId: String): Flow<List<LeaderUserDataModel>>

    class Impl(
        private val storageProvider: StorageProvider,
        private val folderIdProvider: FolderIdProvider,
        private val serializableParser: SerializableParser,
    ) : EventFileDataSource {

        private val eventsUpdates = MutableSharedFlow<Unit>(replay = 1).apply {
            tryEmit(Unit)
        }

        private val leaderUsersUpdates = MutableSharedFlow<Unit>(replay = 1).apply {
            tryEmit(Unit)
        }

        override suspend fun createEvent(eventName: String, image: EventImage?): String {
            val dirId = folderIdProvider.generateId()
            try {
                storageProvider.createDirectory(dirId)

                val meta = MetaDataModel(
                    eventName = eventName,
                    image?.extension
                )

                meta.saveMetaToBytes(dirId)

                image?.let {
                    storageProvider.writeFile(
                        "$dirId/image.${it.extension.value}",
                        it.bytes
                    )
                }
                eventsUpdates.emit(Unit)
                return dirId
            } catch (e: Throwable) {
                storageProvider.deleteDirectory(dirId)
                throw e
            }
        }

        override suspend fun updateEvent(
            dirId: String,
            eventName: String,
            image: EventImage?
        ) {
            val meta = getMetaFromBytes(dirId)
            val newMeta = meta.copy(
                eventName = eventName,
                imageExtension = image?.extension
            )


            newMeta.saveMetaToBytes(dirId)

            storageProvider.deleteFiles(dirId, "image*")
            image?.let {
                storageProvider.writeFile(
                    "$dirId/image.${it.extension.value}",
                    it.bytes
                )
            }
            eventsUpdates.emit(Unit)

        }

        override suspend fun deleteEvent(dirId: String) {
            storageProvider.deleteDirectory(dirId)
            eventsUpdates.emit(Unit)

        }

        override suspend fun getEvent(id: String): EventDomainModel {
            val meta = getMetaFromBytes(id)
            val byteArray = getImageBytes(id, meta)
            return EventDomainModel(
                id = id,
                name = meta.eventName,
                byteArray = byteArray
            )
        }

        override suspend fun getEvents(): List<EventDomainModel> {
            val dirs = storageProvider.getDirFilesAndDirsWithRootAllowed("")
            val list = dirs.map {
                val meta = getMetaFromBytes(it)
                val byteArray = getImageBytes(it, meta)
                EventDomainModel(
                    id = it,
                    name = meta.eventName,
                    byteArray = byteArray
                )
            }
            return list
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getEventsFlow(): Flow<List<EventDomainModel>> {
            return eventsUpdates.mapLatest {
                withContext(Dispatchers.IO) {
                    getEvents()
                }
            }
        }

        override suspend fun mergeXls(dirId: String, list: List<ByteArray>) {
            withContext(Dispatchers.IO) {
                val meta = getMetaFromBytes(dirId)

                val allOldUsers = getLeaderUsers(dirId)

                val allNewUsers = list.flatMap { bytes ->
                    parseExcel(bytes)
                }

                val uniqueUsers = allNewUsers
                    .groupBy { it.id }
                    .map { (_, users) ->
                        users.maxByOrNull { it.dateOfVisit.toComparableDate() } ?: users.first()
                    }

                val users = UsersDataModel(
                    users = uniqueUsers,
                    changesVersion = 1,
                    changeDate = ""
                )

                val usersBytes = serializableParser.toByteArray(users, UsersDataModel.serializer())

                val actions = buildHistoryActions(allOldUsers, uniqueUsers)
                val historyEntry = HistoryEntry(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    actions = actions
                )

                val history = getHistory(dirId).toMutableList()
                history += historyEntry

                val historyModel = HistoryDataModel(history)

                val historyBytes = serializableParser.toByteArray(
                    historyModel,
                    HistoryDataModel.serializer()
                )

                storageProvider.writeFile(
                    relativePathToFile = "$dirId/history.json",
                    bytes = historyBytes
                )

                storageProvider.writeFile(
                    relativePathToFile = "$dirId/users.json",
                    bytes = usersBytes
                )
                leaderUsersUpdates.emit(Unit)
            }
        }

        override suspend fun getLeaderUsers(dirId: String): List<LeaderUserDataModel> {
            return withContext(Dispatchers.IO) {
                val path = "$dirId/users.json"
                if (!storageProvider.exists(path)) {
                    return@withContext emptyList()
                }
                val meta = getMetaFromBytes(dirId)
                val usersBytes = storageProvider.readFile(path)
                val users =
                    serializableParser.fromByteArray(usersBytes, UsersDataModel.serializer())
                users.users

            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getLeaderUsersFlow(dirId: String): Flow<List<LeaderUserDataModel>> {
            return leaderUsersUpdates.mapLatest {
                try {
                    getLeaderUsers(dirId)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

        private suspend fun getHistory(dirId: String): List<HistoryEntry> {
            return withContext(Dispatchers.IO) {

                val path = "$dirId/history.json"

                if (!storageProvider.exists(path)) {
                    return@withContext emptyList()
                }
                val bytes = storageProvider.readFile(path)

                val model = serializableParser.fromByteArray(
                    bytes,
                    HistoryDataModel.serializer()
                )

                model.list
            }
        }

        private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        // поменяй формат под свой Excel!

        private fun String?.toComparableDate(): LocalDate {
            return try {
                this?.let { LocalDate.parse(it, formatter) }
                    ?: LocalDate.MIN
            } catch (e: Exception) {
                LocalDate.MIN
            }
        }

        private suspend fun getMetaFromBytes(eventId: String): MetaDataModel {
            val metaBytes = storageProvider.readFile("$eventId/$META_FILE_NAME")
            val meta = serializableParser.fromByteArray(metaBytes, MetaDataModel.serializer())
            return meta
        }

        /* private suspend fun getImageBytes(eventId: String): ByteArray? {
             for (extension in ImageExtension.entries) {
                 val path = "$eventId/image.${extension.value}"
                 if (storageProvider.exists(path)) {
                     return storageProvider.readFile(path)
                 }
             }
             return null
         }
 */

        private suspend fun getImageBytes(
            eventId: String,
            meta: MetaDataModel
        ): ByteArray? {
            val extension = meta.imageExtension ?: return null
            return storageProvider.readFile("$eventId/image.${extension.value}")
        }


        private suspend fun MetaDataModel.saveMetaToBytes(dirId: String) {
            val metaBytes = serializableParser.toByteArray(this, MetaDataModel.serializer())
            storageProvider.writeFile(
                "$dirId/${META_FILE_NAME}",
                metaBytes
            )
        }

        private fun parseExcel(bytes: ByteArray): List<LeaderUserDataModel> {
            val result = mutableListOf<LeaderUserDataModel>()

            ByteArrayInputStream(bytes).use { input ->
                val workbook = XSSFWorkbook(input)
                val sheet = workbook.getSheetAt(0)

                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    val user = LeaderUserDataModel(
                        id = row.getCell(0)?.numericCellValue?.toInt() ?: 0,
                        fullName = row.getCell(1)?.stringCellValue.orEmpty(),
                        age = row.getCell(2)?.numericCellValue?.toInt() ?: 0,
                        company = row.getCell(3)?.stringCellValue,
                        jobTitle = row.getCell(4)?.stringCellValue,
                        role = row.getCell(5)?.stringCellValue.orEmpty(),
                        format = row.getCell(6)?.stringCellValue.orEmpty(),
                        blackMark = row.getCell(7)?.booleanCellValue ?: false,
                        dateOfVisit = row.getCell(8)?.stringCellValue,
                        applicationDate = row.getCell(9)?.stringCellValue.orEmpty(),
                        applicationStatus = row.getCell(10)?.stringCellValue.orEmpty(),
                        email = row.getCell(11)?.stringCellValue.orEmpty(),
                        phone = row.getCell(12)?.stringCellValue.orEmpty(),
                        city = row.getCell(13)?.stringCellValue.orEmpty(),
                        region = row.getCell(14)?.stringCellValue.orEmpty(),
                        placeOfStudy = row.getCell(15)?.stringCellValue.orEmpty(),
                        speciality = row.getCell(16)?.stringCellValue,
                        formOfStudy = row.getCell(17)?.stringCellValue,
                        studyFormat = row.getCell(18)?.stringCellValue,
                        educationLevel = row.getCell(19)?.stringCellValue
                    )

                    result.add(user)
                }
            }

            return result
        }
    }

    companion object {
        const val META_FILE_NAME = "meta.json"
    }
}