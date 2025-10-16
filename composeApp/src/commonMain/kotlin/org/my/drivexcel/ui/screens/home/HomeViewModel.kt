package org.my.drivexcel.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.data.EventsDataSource

class HomeViewModel(
    private val eventsDataSource: EventsDataSource
) : ViewModel() {


    private val viewModelState = MutableStateFlow(
        HomeViewModelState()
    )

    /*val uiState = viewModelState
        .map(HomeViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            viewModelState.value.toUiState(),
        )*/

    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        eventsDataSource.events
    ) { state, events ->
        val homeEvents = events.map { HomeEvent(
            id = it,
            isSelected = it == state.selectedEventId
        ) }
        state.copy(eventsList = homeEvents).toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeViewModelState().toUiState()
    )

    init {
        viewModelScope.launch {
//            while (true) {
                eventsDataSource.update()
//                delay(10000)
//            }
        }
    }


    fun onEventClick(eventId: Int) {
        viewModelState.update {
            it.copy(
                isDetailsOpen = true,
                selectedEventId = eventId
            )
        }
    }

    fun onEventClose() {
        viewModelState.update {
            it.copy(
                isDetailsOpen = false,
                selectedEventId = null
            )
        }
    }

data class HomeViewModelState(
        val eventsList: List<HomeEvent> = emptyList(),
        val isDetailsOpen: Boolean = false,
        val selectedEventId: Int? = null
    ) {

        fun toUiState(): HomeUiState {

            val selected = eventsList.find { it.id == selectedEventId }
            val detailsOpen = selected != null && isDetailsOpen

            return HomeUiState.Home(
                eventsHome = eventsList,
                isDetailsOpen = detailsOpen,
                selectedEvent = selected
                    ?.let { homeEvent -> EventOnDetailsUiState.EventSelected(eventId = homeEvent.id) }
                    ?: EventOnDetailsUiState.NoEventSelected
            )
        }
    }

    sealed interface HomeUiState {
        data class Home(
            val eventsHome: List<HomeEvent>,
            val isDetailsOpen: Boolean,
            val selectedEvent: EventOnDetailsUiState
        ) : HomeUiState

        companion object {
            fun createHomeDefault() = Home(
                eventsHome = emptyList(),
                isDetailsOpen = false,
                selectedEvent = EventOnDetailsUiState.NoEventSelected
            )
        }
    }

    sealed interface EventOnDetailsUiState {
        data object NoEventSelected : EventOnDetailsUiState
        data class EventSelected(
            val eventId: Int
        ) : EventOnDetailsUiState
    }

    data class HomeEvent(
        val id: Int,
        val isSelected: Boolean
    )
}