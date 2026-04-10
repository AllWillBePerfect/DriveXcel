package org.my.drivexcel.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.my.drivexcel.datasource.sources.PreferencesDataSource
import org.my.drivexcel.ui.navigation.model.UserAuthorizedUiState
import org.my.drivexcel.ui.utils.SnackbarManager

class AppViewModel(
    snackbarManager: SnackbarManager,
    preferencesDataSource: PreferencesDataSource
) : ViewModel() {

    val messages = snackbarManager.messages

    val userAuthorizedState: StateFlow<UserAuthorizedUiState> = preferencesDataSource.userAuthorizedFlow
        .map { authorized ->
            if (authorized) UserAuthorizedUiState.Authorized else UserAuthorizedUiState.Unauthorized
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(0),
            UserAuthorizedUiState.Loading
        )

}