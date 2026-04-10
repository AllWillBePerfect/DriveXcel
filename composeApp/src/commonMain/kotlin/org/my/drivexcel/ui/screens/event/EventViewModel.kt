package org.my.drivexcel.ui.screens.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.base.domain.wrapper.FlowResult
import org.my.drivexcel.domain.usecase.ObserveEventUseCase
import org.my.drivexcel.domain.usecase.GetEventUseCase
import org.my.drivexcel.ui.screens.event.models.EventIdUiState
import org.my.drivexcel.ui.screens.event.models.EventUiAction
import org.my.drivexcel.ui.screens.event.models.EventUiEvent
import org.my.drivexcel.ui.screens.event.models.EventUiState

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class EventViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: AppLogger,
    private val getEventUseCase: GetEventUseCase,
    private val observeEventUseCase: ObserveEventUseCase,
) : ViewModel() {

//    private val eventId: String = requireNotNull(savedStateHandle.get<String>("eventId"))

    private val _uiState = MutableStateFlow(
        EventUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EventUiEvent>()
    val events = _events.asSharedFlow()

    private var currentEventId: String? = null


    /*init {

        logger.i("EventViewModel", "init")

        viewModelScope.launch {
            getEventUseCase(eventId)
                .onSuccess { event -> _uiState.update { it.copy(eventName = event.name) } }
                .onFailure { _uiState.update { it.copy(isLoadingError = true, eventName = "Ошибка") } }
        }

    }*/

    private var eventJob: Job? = null

    fun load(eventId: String) {
        if (currentEventId == eventId) return
        logger.i("EventViewModel", "ID: $eventId")

        currentEventId = eventId

        logger.i("EventViewModel", "load: $eventId")

        eventJob?.cancel()

        eventJob = viewModelScope.launch {
            observeEventUseCase(eventId).collect { event ->
                when (event) {
                    is FlowResult.Success -> {
                        _uiState.update {
                            it.copy(
                                idState = EventIdUiState.Initialized(eventId),
                                eventName = event.data.name,
                                isLoadingError = false
                            )
                        }
                    }

                    is FlowResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingError = true,
                                eventName = "Ошибка"
                            )
                        }
                    }

                    FlowResult.Loading -> {}
                }

            }
        }

        /*viewModelScope.launch {
            getEventUseCase(eventId)
                .onSuccess { event ->
                    _uiState.update {
                        it.copy(
                            id = eventId,
                            eventName = event.name,
                            isLoadingError = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingError = true,
                            eventName = "Ошибка"
                        )
                    }
                }
        }*/
    }

    fun onAction(action: EventUiAction) {
        when (action) {
            EventUiAction.OnBackClicked -> viewModelScope.launch {
                _events.emit(EventUiEvent.NavigateBack)
            }

        }
    }
}

