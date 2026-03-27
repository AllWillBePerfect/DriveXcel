package org.my.drivexcel.v4.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.my.drivexcel.domain.models.EventImageMapperV2
import org.my.drivexcel.v4.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.v4.datasource.exception.mapper.LeaderUserDataToDomainMapper
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.model.LeaderUserDomainModel

interface EventRepository {
    suspend fun getEvent(id: String): EventDomainModel
    fun getEventsFlow(): Flow<List<EventDomainModel>>
    suspend fun createEvent(eventName: String, byteArray: ByteArray?): EventDomainModel
    suspend fun updateEvent(id: String, eventName: String, byteArray: ByteArray?)
    suspend fun deleteEvent(id: String)
    suspend fun saveEvents(events: List<EventDomainModel>)
    suspend fun mergeXls(id: String, list: List<ByteArray>)
    fun getLeaderUsersFlow(id: String): Flow<List<LeaderUserDomainModel>>
    suspend fun eventNameExists(name: String): Boolean


    class Impl(
        private val dataSource: EventFileDataSource,
        private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
        private val leaderUserDataToDomainMapper: LeaderUserDataToDomainMapper,
        private val eventImageMapperV2: EventImageMapperV2,
    ) : EventRepository {
        override suspend fun getEvent(id: String): EventDomainModel {
            return handleErrorCall { dataSource.getEvent(id) }
        }

        override fun getEventsFlow(): Flow<List<EventDomainModel>> {
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

        override suspend fun saveEvents(events: List<EventDomainModel>) {

        }

        override suspend fun mergeXls(id: String, list: List<ByteArray>) {
            handleErrorCall {
                dataSource.mergeXls(id, list)
            }
        }

        override fun getLeaderUsersFlow(id: String): Flow<List<LeaderUserDomainModel>> {
            return dataSource.getLeaderUsersFlow(id)
                .map(leaderUserDataToDomainMapper::toDomainList)
        }

        override suspend fun eventNameExists(name: String): Boolean {
            return name == "42"
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