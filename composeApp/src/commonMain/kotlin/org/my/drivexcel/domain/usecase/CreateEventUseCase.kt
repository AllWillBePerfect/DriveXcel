package org.my.drivexcel.domain.usecase

import org.my.drivexcel.domain.exception.EventEditingDomainException
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.wrapper.DomainEitherResult
import org.my.drivexcel.base.domain.ext.withDomainAppResult

class CreateEventUseCase(
    private val repo: EventRepository
) {
    suspend operator fun invoke(
        eventName: String,
        byteArray: ByteArray?
    ): DomainEitherResult<EventDomainModel, EventEditingDomainException> {

        if (eventName.isBlank()) {
            return DomainEitherResult.Error(EventEditingDomainException.EmptyName())
        }

        if (repo.eventNameExists(eventName)) {
            return DomainEitherResult.Error(EventEditingDomainException.NameAlreadyExists(eventName))
        }

        return withDomainAppResult(
            onSuccess = { repo.createEvent(eventName, byteArray) },
            mapError = EventEditingDomainException::Unknown
        )
    }
}

suspend inline fun <T> filesystemTransaction(
    rollback: suspend () -> Unit,
    block: suspend () -> T
): T {

    return try {
        block()
    } catch (e: Throwable) {
        rollback()
        throw e
    }
}

class FileTransactionManager {

    suspend inline fun <T> run(
        rollback: suspend () -> Unit,
        block: suspend () -> T
    ): T {

        return try {
            block()
        } catch (e: Throwable) {
            rollback()
            throw e
        }
    }
}






