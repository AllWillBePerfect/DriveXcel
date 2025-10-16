package org.my.drivexcel.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.datasources.SettingsDataSource
import org.my.drivexcel.platform.datasources.UserSettings

class SettingsViewModel(
    private val settingsDataSource: SettingsDataSource
) : ViewModel() {

    val uiState = settingsDataSource.userSettings.map { SettingsUiState.Loaded(
        nightMode = it.nightMode
    ) }
        .stateIn(
            scope = viewModelScope,
            initialValue = SettingsUiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun switchNightMode(nightMode: UserSettings.NightMode) {
        viewModelScope.launch {
            settingsDataSource.update { settings ->
                settings.copy(
                    nightMode = nightMode
                )
            }
        }
    }

    sealed interface SettingsUiState {
        object Loading : SettingsUiState
        data class Loaded(
            val nightMode: UserSettings.NightMode
        ) : SettingsUiState
    }
}