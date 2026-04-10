package org.my.drivexcel.base.domain.wrapper

sealed class DomainEitherResult<out T, out E> {
    data class Success<T>(val data: T) : DomainEitherResult<T, Nothing>()
    data class Error<E>(val error: E) : DomainEitherResult<Nothing, E>()
}