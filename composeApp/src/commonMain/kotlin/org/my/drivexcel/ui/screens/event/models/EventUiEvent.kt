package org.my.drivexcel.ui.screens.event.models

sealed interface EventUiEvent {
    object NavigateBack : EventUiEvent
}