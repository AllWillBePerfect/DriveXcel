package org.my.drivexcel.base.domain.wrapper

import org.my.drivexcel.base.domain.exception.DomainException

sealed interface DomainResult <out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Failure(val e: DomainException) : DomainResult<Nothing>
}