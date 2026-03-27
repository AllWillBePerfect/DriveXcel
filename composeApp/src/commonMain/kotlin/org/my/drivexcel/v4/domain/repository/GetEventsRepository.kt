package org.my.drivexcel.v4.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.my.drivexcel.v4.datasource.sources.EventFileDataSource
import org.my.drivexcel.v4.domain.model.EventDomainModel

interface GetEventsRepository {
    fun getEventsFlow(): Flow<List<EventDomainModel>>

    class Impl(
        private val dataSource: EventFileDataSource
    ) : GetEventsRepository {
        override fun getEventsFlow(): Flow<List<EventDomainModel>> {
            return dataSource.getEventsFlow()
        }
    }
}