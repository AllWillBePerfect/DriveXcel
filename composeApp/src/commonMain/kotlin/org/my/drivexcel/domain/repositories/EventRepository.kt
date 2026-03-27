package org.my.drivexcel.domain.repositories

import org.my.drivexcel.domain.models.Event
import org.my.drivexcel.domain.models.EventAbsolute
import org.my.drivexcel.domain.models.EventId
import org.my.drivexcel.domain.models.EventImage
import org.my.drivexcel.domain.models.Participant

interface EventRepository {

    suspend fun createEvent(
        name: String,
        image: EventImage?
    )

    suspend fun updateEvent(
        id: EventId,
        name: String,
        image: EventImage?
    )

    suspend fun deleteEvent(id: EventId)

    suspend fun getAllEvents(): List<Event>
    suspend fun getAllEventsAbsolute(): List<EventAbsolute>
    suspend fun getParticipants(id: EventId): List<Participant>
    suspend fun mergeParticipantsExcel(id: EventId, files: List<ByteArray>)


}