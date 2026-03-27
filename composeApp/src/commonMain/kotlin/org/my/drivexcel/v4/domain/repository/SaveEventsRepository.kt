package org.my.drivexcel.v4.domain.repository

import org.my.drivexcel.v4.domain.model.EventDomainModel

interface SaveEventsRepository {

    suspend fun saveEvents(events: List<EventDomainModel>)
}