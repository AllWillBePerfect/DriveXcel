package org.my.drivexcel.v4.domain.usecase

import org.my.drivexcel.data.AppResult
import org.my.drivexcel.v4.base.domain.ext.DomainResult
import org.my.drivexcel.v4.base.domain.ext.runCatchingDomainResult
import org.my.drivexcel.v4.base.domain.ext.withDomainResult
import org.my.drivexcel.v4.domain.exception.EventEditingDomainException
import org.my.drivexcel.v4.domain.repository.EventRepository
import org.my.drivexcel.v4.domain.repository.UpdateEventRepository

class UpdateEventUseCase(
    private val repo: EventRepository
) {

    suspend operator fun invoke(
        id: String,
        eventName: String,
        byteArray: ByteArray?
    ): AppResult<Unit, EventEditingDomainException>  {

        if (eventName.isBlank()) {
            return AppResult.Error(EventEditingDomainException.EmptyName())
        }

        if (repo.eventNameExists(eventName)) {
            return AppResult.Error(EventEditingDomainException.NameAlreadyExists(eventName))
        }

        return withDomainResult(
            onSuccess = {
                repo.updateEvent(
                    id = id,
                    eventName = eventName,
                    byteArray = byteArray
                )
            },
            mapError = EventEditingDomainException::Unknown
        )
    }

}