package org.my.drivexcel.domain.exception

import org.my.drivexcel.base.domain.exception.DomainException


sealed class CreateEventDomainException(
    message: String
) : DomainException(message = message) {
    class EmptyName : CreateEventDomainException(message = "Название не может путь пустым")
    class NameAlreadyExists(eventName: String) : CreateEventDomainException(message = "Директория с таким именем уже существует: {$eventName}")
}