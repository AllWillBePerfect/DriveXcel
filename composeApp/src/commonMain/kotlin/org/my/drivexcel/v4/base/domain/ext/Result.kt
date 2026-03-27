package org.my.drivexcel.v4.base.domain.ext

import kotlinx.coroutines.CancellationException
import org.my.drivexcel.data.AppResult
import org.my.drivexcel.v4.base.domain.exception.DomainException



inline fun <T, E> withDomainResult(
    onSuccess: () -> T,
    mapError: (DomainException) -> E
): AppResult<T, E> {
    return try {
        AppResult.Success(onSuccess())
    } catch (e: DomainException) {
        return AppResult.Error(mapError(e))
    }
}


public inline fun <T, R> T.runCatchingDomain(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: DomainException) {
        Result.failure(e)
    }
}

sealed interface DomainResult <out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Failure(val e: DomainException) : DomainResult<Nothing>
}

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


/*inline fun <T, E : DomainException> domainResult(
    onSuccess: () -> T,
    onFailure: (Throwable) -> E
): AppResult<T, E> {

    return try {

        AppResult.Success(onSuccess())

    } catch (e: DomainException) {

        @Suppress("UNCHECKED_CAST")
        AppResult.Error(e as E)

    } catch (e: Throwable) {

        AppResult.Error(onFailure(e))
    }
}*/
