package org.my.drivexcel.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.my.drivexcel.base.domain.model.NightModeModel
import org.my.drivexcel.datasource.sources.PreferencesDataSource

class SettingsViewModel(
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = combine(
        _uiState,
        preferencesDataSource.nightModeModelFlow
    ) { state, nightMode ->
    state.copy(
        nightModeModel = nightMode,
    )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingsUiState()
    )

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.SwitchNightMode -> switchNightMode(action.nightModeModel)
            SettingsUiAction.UnauthorizeUser -> unauthorizeUser()
            SettingsUiAction.ShowUnauthorizedDialog -> showUnauthorizeDialog()
            SettingsUiAction.CloseUnauthorizedDialog -> closeUnauthorizeDialog()
        }
    }

    private fun switchNightMode(nightModeModel: NightModeModel) {
        viewModelScope.launch {
            preferencesDataSource.switchNightMode(nightModeModel)
        }
    }

    private fun unauthorizeUser() {
        viewModelScope.launch {
            preferencesDataSource.unauthorizeUser()
            closeUnauthorizeDialog()
            _uiEvent.emit(SettingsUiEvent.NavigateToLoginGraph)
        }
    }

    private fun showUnauthorizeDialog() {
        _uiState.update { it.copy(isUnauthorizeDialogEnabled = true) }
    }

    private fun closeUnauthorizeDialog() {
        _uiState.update { it.copy(isUnauthorizeDialogEnabled = false) }
    }

}

data class SettingsUiState(
    val nightModeModel: NightModeModel = NightModeModel.FOLLOW_SYSTEM,
    val isUnauthorizeDialogEnabled: Boolean = false
)

sealed interface SettingsUiAction {
    data class SwitchNightMode(val nightModeModel: NightModeModel) : SettingsUiAction
    data object UnauthorizeUser: SettingsUiAction

    data object ShowUnauthorizedDialog : SettingsUiAction
    data object CloseUnauthorizedDialog : SettingsUiAction
}

sealed interface SettingsUiEvent {
    data object NavigateToLoginGraph : SettingsUiEvent
}