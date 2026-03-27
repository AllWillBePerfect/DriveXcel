package org.my.drivexcel.v4.domain.repository

import org.my.drivexcel.v4.datasource.exception.mapper.DataExceptionToDomainMapper
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource
import org.my.drivexcel.v4.domain.model.EventDomainModel

interface GetEventRepository {

    suspend fun getEvent(id: String): EventDomainModel

    class Impl(
        private val dataSource: EventFileDataSource,
        private val dataExceptionToDomainMapper: DataExceptionToDomainMapper,
        ) : GetEventRepository {
        override suspend fun getEvent(id: String): EventDomainModel {
            return try {
                dataSource.getEvent(id)
            } catch (e: Exception) {
               throw dataExceptionToDomainMapper.toDomain(e)
            }
        }
    }
}