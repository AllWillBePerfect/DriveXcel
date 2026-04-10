package org.my.drivexcel.domain.exception

import org.my.drivexcel.base.domain.exception.DomainException

sealed class EventEditingDomainException(
    cause: Throwable? = null,
    message: String? = null
) : DomainException(cause, message) {

    class EmptyName : EventEditingDomainException()
    class NameAlreadyExists(eventName: String) : EventEditingDomainException(message = eventName)
    class Unknown(cause: Exception) : EventEditingDomainException(cause = cause)
}

