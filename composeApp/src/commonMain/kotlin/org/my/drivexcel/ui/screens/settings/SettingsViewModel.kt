package org.my.drivexcel.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.my.drivexcel.base.domain.model.NightModeModel
import org.my.drivexcel.datasource.sources.PreferencesDataSource

class SettingsViewModel(
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {


    val uiState = preferencesDataSource.nightModeModelFlow.map { nightModeModel ->
        SettingsUiState(
            nightModeModel = nightModeModel
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
            _uiEvent.emit(SettingsUiEvent.NavigateToLoginGraph)
        }
    }

}

data class SettingsUiState(
    val nightModeModel: NightModeModel = NightModeModel.FOLLOW_SYSTEM
)

sealed interface SettingsUiAction {
    data class SwitchNightMode(val nightModeModel: NightModeModel) : SettingsUiAction
    data object UnauthorizeUser: SettingsUiAction
}

sealed interface SettingsUiEvent {
    data object NavigateToLoginGraph : SettingsUiEvent
}