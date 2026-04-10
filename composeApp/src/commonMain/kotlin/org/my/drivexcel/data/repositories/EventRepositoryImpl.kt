package org.my.drivexcel.data.repositories

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapLatest
import org.my.drivexcel.data.mappers.LeaderUserDataToDomainMapper
import org.my.drivexcel.data.models.HistoryDataModel
import org.my.drivexcel.data.models.HistoryEntry
import org.my.drivexcel.data.models.MetaDataModel
import org.my.drivexcel.data.models.UsersDataModel
import org.my.drivexcel.data.models.buildHistoryActions
import org.my.drivexcel.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.datasource.sources.EventMetaDataSource
import org.my.drivexcel.datasource.sources.HistoryDataSource
import org.my.drivexcel.datasource.sources.UsersDataSource
import org.my.drivexcel.platform.FolderIdProvider
import org.my.drivexcel.domain.parser.EventImageMapperV2
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.parser.ExcelParser
import org.my.drivexcel.domain.repository.EventRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.collections.plus

class EventRepositoryImpl(
    private val eventMetaDataSource: EventMetaDataSource,
    private val usersDataSource: UsersDataSource,
    private val historyDataSource: HistoryDataSource,
    private val excelParser: ExcelParser,
    private val folderIdProvider: FolderIdProvider,
    private val eventImageMapperV2: EventImageMapperV2,
    private val leaderUserDataToDomainMapper: LeaderUserDataToDomainMapper,
    private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
) : EventRepository {
    private val eventsUpdates = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    private val leaderUsersUpdates = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    override suspend fun getEvent(id: String): EventDomainModel = safe {
        val metaData = eventMetaDataSource.readMeta(id)

        val image = metaData.imageExtension?.let {
            eventMetaDataSource.readImage(id, it.value)
        }

        EventDomainModel(
            id = id,
            name = metaData.eventName,
            byteArray = image
        )
    }

    override suspend fun createEvent(
        eventName: String,
        byteArray: ByteArray?
    ): EventDomainModel = safe {

        val id = folderIdProvider.generateId()

        try {
            eventMetaDataSource.createEventDir(id)

            val metaModel = MetaDataModel(
                eventName = eventName,
                imageExtension = byteArray?.let { eventImageMapperV2.fromByteArray(it).extension }
            )

            eventMetaDataSource.writeMeta(id, metaModel)

            byteArray?.let {
                eventMetaDataSource.writeImage(id, metaModel.imageExtension!!.value, it)
            }

            eventsUpdates.emit(Unit)

            getEvent(id)

        } catch (e: Throwable) {
            eventMetaDataSource.deleteEventDir(id)
            throw e
        }
    }

    override suspend fun updateEvent(
        id: String,
        eventName: String,
        byteArray: ByteArray?
    ) = safe {
        val oldMeta = eventMetaDataSource.readMeta(id)

        val newExtension = byteArray?.let {
            eventImageMapperV2.fromByteArray(it).extension
        }

        val newMeta = oldMeta.copy(
            eventName = eventName,
            imageExtension = newExtension ?: oldMeta.imageExtension
        )

        eventMetaDataSource.writeMeta(id, newMeta)

        eventMetaDataSource.deleteImages(id)

        byteArray?.let {
            newMeta.imageExtension?.let { ext ->
                eventMetaDataSource.writeImage(id, ext.value, it)
            }
        }

        eventsUpdates.emit(Unit)
    }

    override suspend fun deleteEvent(id: String) = safe {
        eventMetaDataSource.deleteEventDir(id)
        eventsUpdates.emit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeEvent(id: String): Flow<EventDomainModel> {
        return eventsUpdates.mapLatest { getEvent(id) }
    }

    override suspend fun getEvents(): List<EventDomainModel> = safe {
        eventMetaDataSource.getAllEventIds().map { getEvent(it) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeEvents(): Flow<List<EventDomainModel>> {
        return eventsUpdates.mapLatest { getEvents() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun mergeUsersFromExcel(
        id: String,
        list: List<ByteArray>
    ) {
        val oldUsers = usersDataSource.readUsers(id)?.users ?: emptyList()

        val parsed = list.flatMap { excelParser.parseToData(it) }

        val uniqueUsers = parsed
            .groupBy { it.id }
            .map { (_, users) ->
                users.maxByOrNull { it.dateOfVisit.toComparableDate() }!!
            }

        val actions = buildHistoryActions(oldUsers, uniqueUsers)

        val historyEntry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            actions = actions
        )

        val existingHistory = historyDataSource.readHistory(id)?.list ?: emptyList()

        historyDataSource.writeHistory(
            id,
            HistoryDataModel(existingHistory + historyEntry)
        )

        usersDataSource.writeUsers(
            id,
            UsersDataModel(
                users = uniqueUsers,
                changesVersion = 1,
                changeDate = ""
            )
        )

        leaderUsersUpdates.emit(Unit)
    }

    override suspend fun getUsers(id: String): List<LeaderUserDomainModel> = safe {
        usersDataSource.readUsers(id)
            ?.users
            ?.map { leaderUserDataToDomainMapper.toDomain(it) }
            ?: emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUsers(id: String): Flow<List<LeaderUserDomainModel>> {
        return leaderUsersUpdates.mapLatest { getUsers(id) }
    }

    override suspend fun eventNameExists(name: String): Boolean = safe {
        getEvents().any { it.name == name }
    }

    override suspend fun saveUsers(
        id: String,
        users: List<LeaderUserDomainModel>
    ) = safe {

        val version = usersDataSource.readUsers(id)?.changesVersion ?: 1
        val dataUsers = users.map { leaderUserDataToDomainMapper.fromDomain(it) }

        val model = UsersDataModel(
            users = dataUsers,
            changesVersion = version,
            changeDate = ""
        )

        this.usersDataSource.writeUsers(id, model)

        leaderUsersUpdates.emit(Unit)
    }

    override suspend fun appendHistory(
        id: String,
        entry: HistoryEntry
    ) = safe {

        val existing = historyDataSource.readHistory(id)?.list ?: emptyList()

        val updated = existing + entry

        historyDataSource.writeHistory(
            id,
            HistoryDataModel(updated)
        )
    }

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private fun String?.toComparableDate(): LocalDate {
        return try {
            this?.let { LocalDate.parse(it, formatter) }
                ?: LocalDate.MIN
        } catch (e: Exception) {
            LocalDate.MIN
        }
    }

    private suspend fun <T> safe(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            throw dataExceptionToDomainMapper.toDomain(e)
        }
    }
}