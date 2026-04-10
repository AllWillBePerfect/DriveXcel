package org.my.drivexcel.base.domain.ext

import org.my.drivexcel.base.domain.exception.DomainException
import org.my.drivexcel.base.domain.wrapper.DomainEitherResult

inline fun <T, E, R> DomainEitherResult<T, E>.map(transform: (T) -> R): DomainEitherResult<R, E> =
    when (this) {
        is DomainEitherResult.Success -> DomainEitherResult.Success(transform(data))
        is DomainEitherResult.Error -> this
    }

inline fun <T, E, R> DomainEitherResult<T, E>.mapError(transform: (E) -> R): DomainEitherResult<T, R> =
    when (this) {
        is DomainEitherResult.Success -> this
        is DomainEitherResult.Error -> DomainEitherResult.Error(transform(error))
    }

inline fun <T, E> DomainEitherResult<T, E>.onSuccess(action: (T) -> Unit): DomainEitherResult<T, E> {
    if (this is DomainEitherResult.Success) action(data)
    return this
}

inline fun <T, E> DomainEitherResult<T, E>.onFailure(action: (E) -> Unit): DomainEitherResult<T, E> {
    if (this is DomainEitherResult.Error) action(error)
    return this
}

inline fun <T, E, R> DomainEitherResult<T, E>.fold(
    onSuccess: (T) -> R,
    onError: (E) -> R
): R = when (this) {
    is DomainEitherResult.Success -> onSuccess(data)
    is DomainEitherResult.Error -> onError(error)
}

inline fun <T, E> withDomainAppResult(
    onSuccess: () -> T,
    mapError: (DomainException) -> E
): DomainEitherResult<T, E> {
    return try {
        DomainEitherResult.Success(onSuccess())
    } catch (e: DomainException) {
        return DomainEitherResult.Error(mapError(e))
    }
}