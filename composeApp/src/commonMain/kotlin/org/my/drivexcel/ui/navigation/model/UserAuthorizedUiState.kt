package org.my.drivexcel.ui.navigation.model

sealed interface UserAuthorizedUiState {
    data object Loading : UserAuthorizedUiState
    data object Authorized : UserAuthorizedUiState
    data object Unauthorized : UserAuthorizedUiState
}