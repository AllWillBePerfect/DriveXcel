package org.my.drivexcel.v4.base.domain.usecase

import org.my.drivexcel.v4.base.domain.exception.DomainException
import org.my.drivexcel.v4.base.domain.exception.UnknownDomainException

class UseCaseExecutor {
    fun <R> execute(
        useCase: UseCase<Unit, R>,
        onResult: (R) -> Unit = {},
        onException: (DomainException) -> Unit = {}
    ) = execute(useCase, Unit, onResult, onException)

    fun <T, R> execute(
        useCase: UseCase<T, R>,
        value: T,
        onResult: (R) -> Unit = {},
        onException: (DomainException) -> Unit = {}
    ) {
        try {
            useCase.execute(value, onResult)
        } catch (throwable: Throwable) {
            val domainException = ((throwable as? DomainException)) ?: UnknownDomainException(throwable)
            onException(domainException)
        }
    }
}