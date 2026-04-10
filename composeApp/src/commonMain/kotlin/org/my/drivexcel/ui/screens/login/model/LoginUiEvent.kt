package org.my.drivexcel.ui.screens.login.model

sealed interface LoginUiEvent {
    data object NavigateToMainGraph : LoginUiEvent
}