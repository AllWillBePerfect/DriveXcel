package org.my.drivexcel.v4.base.domain.usecase.v3

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in P, R> {

    operator fun invoke(params: P): Flow<R> =
        execute(params)

    protected abstract fun execute(params: P): Flow<R>
}
