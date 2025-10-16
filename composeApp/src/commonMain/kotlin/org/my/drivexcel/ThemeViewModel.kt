package org.my.drivexcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.my.drivexcel.platform.datasources.SettingsDataSource
import org.my.drivexcel.platform.datasources.UserSettings
import org.my.drivexcel.platform.utils.AppLogger
import org.my.drivexcel.platform.utils.PlatformProvider

class ThemeViewModel(
    private val settingsDataSource: SettingsDataSource,
    private val appLogger: AppLogger,
    private val platformProvider: PlatformProvider
) : ViewModel() {

    val us = settingsDataSource.userSettings.map { it.nightMode }
        .stateIn(
            scope = viewModelScope,
            initialValue = UserSettings.NightMode.FOLLOW_SYSTEM,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    val userSettings = settingsDataSource.userSettings.map {
        AppUiState.Success(userSettings = it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = AppUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    init {
        appLogger.d("ThemeViewModel", "init ${platformProvider.currentPlatform()}")
    }

    fun switchNightTheme(nightMode: UserSettings.NightMode) {
        viewModelScope.launch {
            settingsDataSource.update { settings ->
                settings.copy(
                    nightMode = nightMode
                )
            }
        }
    }
}

sealed class AppUiState {
    object Loading : AppUiState()
    data class Success(val userSettings: UserSettings) : AppUiState() {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (userSettings.nightMode) {
                UserSettings.NightMode.YES -> true
                UserSettings.NightMode.NO -> false
                UserSettings.NightMode.FOLLOW_SYSTEM -> isSystemDarkTheme
            }
    }


    val shouldKeepSplashScreen: Boolean get() = this is Loading
    open fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}