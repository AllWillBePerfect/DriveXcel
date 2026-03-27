package org.my.drivexcel.v4.ui.screens.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.v4.base.domain.ext.onFailure
import org.my.drivexcel.v4.base.domain.ext.onSuccess
import org.my.drivexcel.v4.domain.usecase.GetEventUseCase
import org.my.drivexcel.v4.ui.screens.event.models.EventUiAction
import org.my.drivexcel.v4.ui.screens.event.models.EventUiEvent
import org.my.drivexcel.v4.ui.screens.event.models.EventUiState

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class EventViewModel(

    savedStateHandle: SavedStateHandle,
    private val logger: AppLogger,
    private val getEventUseCase: GetEventUseCase
) : ViewModel() {

    private val eventId: String = requireNotNull(savedStateHandle.get<String>("eventId"))

    private val _uiState = MutableStateFlow(
        EventUiState(
            id = eventId
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EventUiEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            getEventUseCase(eventId)
                .onSuccess { event -> _uiState.update { it.copy(eventName = event.name) } }
                .onFailure { _uiState.update { it.copy(isLoadingError = true, eventName = "Ошибка") } }
        }
    }

    fun onAction(action: EventUiAction) {
        when (action) {
            EventUiAction.OnBackClicked -> viewModelScope.launch {
                _events.emit(EventUiEvent.NavigateBack)
            }

        }
    }
}

