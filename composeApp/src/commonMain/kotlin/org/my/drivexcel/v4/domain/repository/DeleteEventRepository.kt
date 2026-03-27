package org.my.drivexcel.v4.domain.repository

import org.my.drivexcel.v4.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource

interface DeleteEventRepository {

    suspend fun deleteEvent(id: String)

    class Impl(
        private val dataSource: EventFileDataSource,
        private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
        ) : DeleteEventRepository {
        override suspend fun deleteEvent(id: String) {
            try {
                return dataSource.deleteEvent(id)
            } catch (e: Exception) {
                throw dataExceptionToDomainMapper.toDomain(e)
            }
        }
    }
}


