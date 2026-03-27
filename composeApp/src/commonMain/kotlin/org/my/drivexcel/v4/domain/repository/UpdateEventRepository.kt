package org.my.drivexcel.v4.domain.repository

import org.my.drivexcel.domain.models.EventImageMapperV2
import org.my.drivexcel.v4.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource

interface UpdateEventRepository {

    suspend fun updateEvent(id: String, eventName: String, byteArray: ByteArray?)

    class Impl(
        private val dataSource: EventFileDataSource,
        private val eventImageMapperV2: EventImageMapperV2,
        private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
    ) : UpdateEventRepository {
        override suspend fun updateEvent(
            id: String,
            eventName: String,
            byteArray: ByteArray?
        ) {
            try {
                dataSource.updateEvent(
                    dirId = id,
                    eventName = eventName,
                    image = byteArray?.let { eventImageMapperV2.fromByteArray(it) }
                )
            } catch (e: Exception) {
               throw dataExceptionToDomainMapper.toDomain(e)
            }
        }
    }
}