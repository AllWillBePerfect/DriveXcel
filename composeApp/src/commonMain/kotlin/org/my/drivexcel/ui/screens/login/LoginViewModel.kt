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
import org.my.drivexcel.domain.demo.EventDemoDataGenerator
import org.my.drivexcel.ui.screens.login.model.LoginUiAction
import org.my.drivexcel.ui.screens.login.model.LoginUiEvent
import org.my.drivexcel.ui.screens.login.model.LoginUiState

class LoginViewModel(
    private val preferencesDataSource: PreferencesDataSource,
    private val eventDemoDataGenerator: EventDemoDataGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            LoginUiAction.AuthorizeUser -> authorizeUser()
            LoginUiAction.AuthorizeTestUser -> authorizeTestUser()
        }
    }

    private fun authorizeUser() {
        _uiState.update { it.copy(clickPerformed = true) }
        viewModelScope.launch {
            preferencesDataSource.setLocalDirType()
            preferencesDataSource.authorizeUser()
            _uiEvent.emit(LoginUiEvent.NavigateToMainGraph)
        }
    }

    private fun authorizeTestUser() {
        _uiState.update { it.copy(clickPerformed = true) }
        viewModelScope.launch {
            preferencesDataSource.setTestDirType()
            eventDemoDataGenerator.generateTestData()
            preferencesDataSource.authorizeUser()
            _uiEvent.emit(LoginUiEvent.NavigateToMainGraph)
        }
    }

}

