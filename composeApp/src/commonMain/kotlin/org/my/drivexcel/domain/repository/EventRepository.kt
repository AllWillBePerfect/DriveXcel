package org.my.drivexcel.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.parser.EventImageMapperV2
import org.my.drivexcel.data.mappers.LeaderUserDataToDomainMapper
import org.my.drivexcel.data.models.HistoryEntry
import org.my.drivexcel.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.datasource.sources.EventFileDataSource

interface EventRepository {
    suspend fun getEvent(id: String): EventDomainModel
    suspend fun createEvent(eventName: String, byteArray: ByteArray?): EventDomainModel
    suspend fun updateEvent(id: String, eventName: String, byteArray: ByteArray?)
    suspend fun deleteEvent(id: String)
    fun observeEvent(id: String): Flow<EventDomainModel>
    suspend fun getEvents(): List<EventDomainModel>
    fun observeEvents(): Flow<List<EventDomainModel>>

    suspend fun mergeUsersFromExcel(id: String, list: List<ByteArray>)
    suspend fun getUsers(id: String): List<LeaderUserDomainModel>
    fun observeUsers(id: String): Flow<List<LeaderUserDomainModel>>
    suspend fun eventNameExists(name: String): Boolean

    suspend fun saveUsers(id: String, users: List<LeaderUserDomainModel>)
    suspend fun appendHistory(id: String, entry: HistoryEntry)


    class Impl(
        private val dataSource: EventFileDataSource,
        private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
        private val leaderUserDataToDomainMapper: LeaderUserDataToDomainMapper,
        private val eventImageMapperV2: EventImageMapperV2,
    ) : EventRepository {
        override suspend fun getEvent(id: String): EventDomainModel {
            return handleErrorCall { dataSource.getEvent(id) }
        }

        override fun observeEvent(id: String): Flow<EventDomainModel> {
            return dataSource.getEventFlow(id)
        }

        override suspend fun getEvents(): List<EventDomainModel> {
            TODO("Not yet implemented")
        }

        override fun observeEvents(): Flow<List<EventDomainModel>> {
            return dataSource.getEventsFlow()
        }

        override suspend fun createEvent(
            eventName: String,
            byteArray: ByteArray?
        ): EventDomainModel {
            return handleErrorCall {
                val id = dataSource.createEvent(
                    eventName = eventName,
                    image = byteArray?.let { eventImageMapperV2.fromByteArray(it) })
                dataSource.getEvent(id)
            }
        }

        override suspend fun updateEvent(
            id: String,
            eventName: String,
            byteArray: ByteArray?
        ) {
            handleErrorCall {
                dataSource.updateEvent(
                    dirId = id,
                    eventName = eventName,
                    image = byteArray?.let { eventImageMapperV2.fromByteArray(it) }
                )
            }
        }

        override suspend fun deleteEvent(id: String) {
            handleErrorCall { dataSource.deleteEvent(id) }
        }

        override suspend fun mergeUsersFromExcel(id: String, list: List<ByteArray>) {
            handleErrorCall {
                dataSource.mergeXls(id, list)
            }
        }

        override suspend fun getUsers(id: String): List<LeaderUserDomainModel> {
            TODO("Not yet implemented")
        }

        override fun observeUsers(id: String): Flow<List<LeaderUserDomainModel>> {
            return dataSource.getLeaderUsersFlow(id)
                .map(leaderUserDataToDomainMapper::toDomainList)
        }

        override suspend fun eventNameExists(name: String): Boolean {
            return name == "42"
        }

        override suspend fun saveUsers(
            id: String,
            users: List<LeaderUserDomainModel>
        ) {
            TODO("Not yet implemented")
        }

        override suspend fun appendHistory(
            id: String,
            entry: HistoryEntry
        ) {
            TODO("Not yet implemented")
        }

        private suspend fun <T> handleErrorCall(block: suspend () -> T): T {
            return try {
                block()
            } catch (e: Exception) {
                throw dataExceptionToDomainMapper.toDomain(e)
            }
        }
    }

}