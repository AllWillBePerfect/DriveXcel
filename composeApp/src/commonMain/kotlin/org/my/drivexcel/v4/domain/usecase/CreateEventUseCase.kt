package org.my.drivexcel.v4.domain.usecase

import org.my.drivexcel.data.AppResult
import org.my.drivexcel.v4.base.domain.ext.withDomainResult
import org.my.drivexcel.v4.domain.exception.EventEditingDomainException
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.repository.EventRepository

class CreateEventUseCase(
    private val repo: EventRepository
) {
    suspend operator fun invoke(
        eventName: String,
        byteArray: ByteArray?
    ): AppResult<EventDomainModel, EventEditingDomainException> {

        if (eventName.isBlank()) {
            return AppResult.Error(EventEditingDomainException.EmptyName())
        }

        if (repo.eventNameExists(eventName)) {
            return AppResult.Error(EventEditingDomainException.NameAlreadyExists(eventName))
        }

        return withDomainResult(
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






