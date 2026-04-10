package org.my.drivexcel.base.domain.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform
import org.my.drivexcel.base.domain.wrapper.FlowResult


fun <T> Flow<T>.toFlowResult(): Flow<FlowResult<T>> {
    return transform<T, FlowResult<T>> { value ->
        emit(FlowResult.Success(value))
    }
        .onStart { emit(FlowResult.Loading) }
        .retryWhen { cause, _ ->
            emit(FlowResult.Error(cause))
            false
        }
}

/*fun <T> Flow<T>.toFlowResult(): Flow<FlowResult<T>> {
    return transform<T, FlowResult<T>> { value ->
        emit(FlowResult.Success(value))
    }
        .onStart { emit(FlowResult.Loading) }
        .retryWhen { cause, _ ->
            emit(FlowResult.Error(cause))
            delay(500)
            true
        }
}*/
