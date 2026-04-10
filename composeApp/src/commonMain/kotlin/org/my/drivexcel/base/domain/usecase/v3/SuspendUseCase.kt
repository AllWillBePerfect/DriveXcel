package org.my.drivexcel.base.domain.usecase.v3

abstract class SuspendUseCase<in P, out R> {

    suspend operator fun invoke(params: P): Result<R> {
        return runCatching { execute(params) }
    }

    protected abstract suspend fun execute(params: P): R
}



