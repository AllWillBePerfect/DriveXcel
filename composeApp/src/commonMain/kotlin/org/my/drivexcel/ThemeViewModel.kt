package org.my.drivexcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.platform.PlatformProvider
import org.my.drivexcel.base.domain.model.NightModeModel
import org.my.drivexcel.datasource.sources.PreferencesDataSource

class ThemeViewModel(
    private val appLogger: AppLogger,
    private val platformProvider: PlatformProvider,
    private val preferencesDataSource: PreferencesDataSource

) : ViewModel() {

    val userSettings = preferencesDataSource.nightModeModelFlow.map {
        AppUiState.Success(
            nightMode = it
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = AppUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    init {
        appLogger.d("ThemeViewModel", "init ${platformProvider.currentPlatform()}")
    }

}

sealed class AppUiState {
    object Loading : AppUiState()
    data class Success(val nightMode: NightModeModel) : AppUiState() {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (nightMode) {
                NightModeModel.NIGHT -> true
                NightModeModel.DAY -> false
                NightModeModel.FOLLOW_SYSTEM -> isSystemDarkTheme
            }
    }


    val shouldKeepSplashScreen: Boolean get() = this is Loading
    open fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}