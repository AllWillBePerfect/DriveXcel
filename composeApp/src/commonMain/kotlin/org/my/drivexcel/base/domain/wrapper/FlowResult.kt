package org.my.drivexcel.base.domain.wrapper

sealed interface FlowResult<out T> {

    data object Loading : FlowResult<Nothing>

    data class Success<T>(
        val data: T
    ) : FlowResult<T>

    data class Error(
        val throwable: Throwable
    ) : FlowResult<Nothing>
}