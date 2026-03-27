package org.my.drivexcel.domain.usecases

import org.my.drivexcel.data.AppResult
import org.my.drivexcel.data.EventDirErrors
import org.my.drivexcel.data.v3.utils.EventImageMapper
import org.my.drivexcel.domain.repositories.EventRepository

interface CreateEventUseCaseOld {
    suspend operator fun invoke(
        name: String,
        image: ByteArray?
    ): AppResult<Unit, EventDirErrors>

    class Impl(
        private val repository: EventRepository,
        private val imageMapper: EventImageMapper
    ) : CreateEventUseCaseOld {
        override suspend fun invoke(
            name: String,
            image: ByteArray?
        ): AppResult<Unit, EventDirErrors> {

            val eventImage = image?.let { imageMapper.fromBytes(it) }

            return try {
                repository.createEvent(
                    name = name,
                    image = eventImage
                )
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(EventDirErrors.Unhandled(e))
            }

        }
    }
}