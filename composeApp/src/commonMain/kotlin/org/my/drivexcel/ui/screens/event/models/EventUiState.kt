package org.my.drivexcel.ui.screens.event.models

data class EventUiState(
    val idState: EventIdUiState = EventIdUiState.Empty,
    val eventName: String = "",
    val isLoadingError: Boolean = false
)

sealed interface EventIdUiState {
    data object Empty : EventIdUiState
    data class Initialized(val id: String): EventIdUiState
}