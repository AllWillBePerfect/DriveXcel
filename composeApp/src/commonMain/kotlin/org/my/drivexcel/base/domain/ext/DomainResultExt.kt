package org.my.drivexcel.base.domain.ext

import org.my.drivexcel.base.domain.exception.DomainException
import org.my.drivexcel.base.domain.wrapper.DomainResult

inline fun <T> DomainResult<T>.fold(
    onSuccess: (T) -> Unit,
    onFailure: (Exception) -> Unit
) {
    when (this) {
        is DomainResult.Success -> onSuccess(data)
        is DomainResult.Failure -> onFailure(e)
    }
}

inline fun <T> DomainResult<T>.onSuccess(block: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) block(data)
    return this
}

inline fun <T> DomainResult<T>.onFailure(block: (DomainException) -> Unit): DomainResult<T> {
    if (this is DomainResult.Failure) block(e)
    return this
}

inline fun <T, R> T.runCatchingDomainResult(block: T.() -> R): DomainResult<R> {
    return try {
        DomainResult.Success(block())
    } catch (e: DomainException) {
        DomainResult.Failure(e)
    }
}