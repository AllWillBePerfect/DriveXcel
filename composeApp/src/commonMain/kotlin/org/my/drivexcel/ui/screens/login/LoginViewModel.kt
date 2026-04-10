package org.my.drivexcel.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.datasource.sources.PreferencesDataSource
import org.my.drivexcel.ui.screens.login.model.LoginUiAction
import org.my.drivexcel.ui.screens.login.model.LoginUiEvent
import org.my.drivexcel.ui.screens.login.model.LoginUiState

class LoginViewModel(
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            LoginUiAction.AuthorizeUser -> authorizeUser()
        }
    }

    private fun authorizeUser() {
        _uiState.update { it.copy(clickPerformed = true) }
        viewModelScope.launch {
            preferencesDataSource.authorizeUser()
            _uiEvent.emit(LoginUiEvent.NavigateToMainGraph)
        }
    }
}

