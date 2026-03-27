package org.my.drivexcel.v4.base.domain.usecase.v2

abstract class BaseSuspendUseCase<in T, R> : SuspendUseCase<T, R> {
    final override suspend fun invoke(input: T): R =
        execute(input)

    protected abstract suspend fun execute(request: T): R

}