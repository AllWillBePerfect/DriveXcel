package org.my.drivexcel.ui.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SnackbarManager {

    private val _messages = MutableSharedFlow<SnackbarAction>()
    val messages = _messages.asSharedFlow()

    suspend fun send(action: SnackbarAction) {
        _messages.emit(action)
    }
}

sealed interface SnackbarAction {
    data object EventCreated : SnackbarAction
    data object EventDeleted : SnackbarAction
    data object EventUpdated : SnackbarAction

    data object LeaderUsersSaved : SnackbarAction

    data class ExceptionAppear(val e: Throwable) : SnackbarAction

}

fun SnackbarAction.toMessage() = when (this) {
    SnackbarAction.EventCreated -> "Мероприятие создано"
    SnackbarAction.EventDeleted -> "Мероприятие удалено"
    SnackbarAction.EventUpdated -> "Мероприятие обновлено"
    SnackbarAction.LeaderUsersSaved -> "Пользователи сохранены"
    is SnackbarAction.ExceptionAppear -> "Произошла ошибка: ${e.cause?.javaClass?.simpleName}"
}