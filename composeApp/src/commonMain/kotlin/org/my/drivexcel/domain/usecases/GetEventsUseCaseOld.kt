package org.my.drivexcel.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.my.drivexcel.domain.models.EventAbsolute
import org.my.drivexcel.domain.repositories.EventRepository

interface GetEventsUseCaseOld {
    operator fun invoke(): Flow<List<EventAbsolute>>

    class Impl(
        private val repository: EventRepository
    ) : GetEventsUseCaseOld {
        override fun invoke(): Flow<List<EventAbsolute>> {
            return flow {
                val list = repository.getAllEventsAbsolute()
                emit(list)
            }
        }

    }
}