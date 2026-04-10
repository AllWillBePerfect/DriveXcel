package org.my.drivexcel.base.domain.usecase.v2

import kotlinx.coroutines.flow.Flow

abstract class BaseFlowUseCase<in T, R> : FlowUseCase<T, R> {

    final override fun invoke(input: T): Flow<R> {
        return execute(input)
    }

    protected abstract fun execute(request: T): Flow<R>

}