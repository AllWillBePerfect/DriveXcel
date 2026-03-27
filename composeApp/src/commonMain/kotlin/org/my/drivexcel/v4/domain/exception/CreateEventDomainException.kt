package org.my.drivexcel.v4.domain.exception

import org.my.drivexcel.v4.base.domain.exception.DomainException


sealed class CreateEventDomainException(
    message: String
) : DomainException(message = message) {
    class EmptyName : CreateEventDomainException(message = "Название не может путь пустым")
    class NameAlreadyExists(eventName: String) : CreateEventDomainException(message = "Директория с таким именем уже существует: {$eventName}")
}