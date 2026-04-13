package org.my.drivexcel.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.base.domain.exception.DomainException
import org.my.drivexcel.base.domain.ext.onFailure
import org.my.drivexcel.base.domain.ext.onSuccess
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.usecase.DeleteEventUseCase
import org.my.drivexcel.domain.usecase.ObserveEventsUseCase
import org.my.drivexcel.ui.screens.events.EventsUiEvent.CreateEvent
import org.my.drivexcel.ui.screens.events.EventsUiEvent.EventPressed
import org.my.drivexcel.ui.screens.events.EventsUiEvent.UpdateEvent
import org.my.drivexcel.ui.utils.SnackbarAction
import org.my.drivexcel.ui.utils.SnackbarManager

class EventsViewModel(
    private val logger: AppLogger,
    private val snackbarManager: SnackbarManager,
    private val observeEventsUseCase: ObserveEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUIState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EventsUiEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeEventsUseCase(Unit).collect { events ->
                _uiState.update {
                    it.copy(events = events, initEmptyList = events.isEmpty())
                }
            }
        }
    }

    fun onAction(action: EventsUIAction) {
        when (action) {
            is EventsUIAction.DeleteEvent -> {
                viewModelScope.launch {
                    val id = uiState.value.eventIdToDelete ?: return@launch
                    deleteEventUseCase(id)
                        .onSuccess {
                            snackbarManager.send(SnackbarAction.EventDeleted)
                        }
                        .onFailure(::deleteErrorHandle)
                    hideDeleteDialog()
                }
            }

            is EventsUIAction.UpdateEvent -> viewModelScope.launch {
                _events.emit(UpdateEvent(action.id))
            }

            EventsUIAction.CreateEvent -> viewModelScope.launch {
                _events.emit(CreateEvent)
            }

            is EventsUIAction.EventPressed -> viewModelScope.launch {
                _events.emit(EventPressed(action.id))
            }

            is EventsUIAction.OnDeleteClicked -> {
                _uiState.update { it.copy(eventIdToDelete = action.id) }
                showDeleteDialog()
            }

            EventsUIAction.OnDeleteDismissClicked -> hideDeleteDialog()
            EventsUIAction.OnEditingClicked -> {
                _uiState.update { it.copy(
                    isEditingMode = !it.isEditingMode
                ) }
            }
        }
    }

    private fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    private fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    private fun deleteErrorHandle(e: DomainException) {
        viewModelScope.launch {
            snackbarManager.send(SnackbarAction.ExceptionAppear(e))
        }
        logger.e(
            "EventEditorViewModel",
            "Произошла ошибка при удалении элемента: $e"
        )
    }

}

data class EventsUIState(
    val isLoading: Boolean = false,
    val events: List<EventDomainModel> = listOf(),
    val initEmptyList: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val eventIdToDelete: String? = null,
    val isEditingMode: Boolean = false
)

sealed interface EventsUIAction {
    object DeleteEvent : EventsUIAction
    data class UpdateEvent(val id: String) : EventsUIAction
    object CreateEvent : EventsUIAction
    data class EventPressed(val id: String) : EventsUIAction
    data class OnDeleteClicked(val id: String) : EventsUIAction
    object OnDeleteDismissClicked : EventsUIAction
    object OnEditingClicked : EventsUIAction
}

sealed interface EventsUiEvent {
    data class UpdateEvent(val id: String) : EventsUiEvent
    object CreateEvent : EventsUiEvent
    data class EventPressed(val id: String) : EventsUiEvent
}