package org.my.drivexcel.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ActionsManager {

    private val _snackbarActions = MutableSharedFlow<SnackbarActions>(
        replay = 1
    )
    val snackbarActions = _snackbarActions.asSharedFlow()

    fun eventCreated() {
        _snackbarActions.tryEmit(SnackbarActions.EventCreated)
    }

    fun eventChanged() {
        _snackbarActions.tryEmit(SnackbarActions.EventChanged)
    }

    fun eventFailed(message: String) {
        _snackbarActions.tryEmit(SnackbarActions.EventFailed(message))
    }

    fun info(message: String) {
        _snackbarActions.tryEmit(SnackbarActions.Info(message))
    }

}

sealed class SnackbarActions(val message: String) {
    data object EventCreated : SnackbarActions("Директория успешно создана")
    data object EventChanged : SnackbarActions("Директория успешно изменена")
    data class EventFailed(val errorCause: String) :
        SnackbarActions("При создании директории произошла ошибка: $errorCause")
    data class Info(val info: String) : SnackbarActions(info)
}