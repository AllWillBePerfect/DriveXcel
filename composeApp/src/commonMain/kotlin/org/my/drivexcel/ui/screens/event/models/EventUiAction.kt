package org.my.drivexcel.ui.screens.event.models

sealed interface EventUiAction {
    object OnBackClicked : EventUiAction
}