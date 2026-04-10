package org.my.drivexcel.domain.usecase

import org.my.drivexcel.domain.exception.EventEditingDomainException
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.wrapper.DomainEitherResult
import org.my.drivexcel.base.domain.ext.withDomainAppResult

class UpdateEventUseCase(
    private val repo: EventRepository
) {

    suspend operator fun invoke(
        id: String,
        eventName: String,
        byteArray: ByteArray?
    ): DomainEitherResult<Unit, EventEditingDomainException>  {

        if (eventName.isBlank()) {
            return DomainEitherResult.Error(EventEditingDomainException.EmptyName())
        }

        if (repo.eventNameExists(eventName)) {
            return DomainEitherResult.Error(EventEditingDomainException.NameAlreadyExists(eventName))
        }

        return withDomainAppResult(
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