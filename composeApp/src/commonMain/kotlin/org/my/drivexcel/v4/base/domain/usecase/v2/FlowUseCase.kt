package org.my.drivexcel.v4.base.domain.usecase.v2

import kotlinx.coroutines.flow.Flow

interface FlowUseCase<in T, out R> {
    operator fun invoke(input: T): Flow<R>
}