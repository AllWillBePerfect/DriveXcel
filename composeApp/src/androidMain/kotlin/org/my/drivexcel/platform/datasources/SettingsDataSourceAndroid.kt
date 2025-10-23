package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

//FIXME make correct implementation
class SettingsDataSourceAndroid : SettingsDataSource {

    private val settingsData = MutableStateFlow(value = UserSettings.createDefault())

    override val userSettings: Flow<UserSettings>
        get() = settingsData.asStateFlow()

    override suspend fun update(transform: (UserSettings) -> UserSettings) = withContext(Dispatchers.IO)  {
        val newSettings = transform(settingsData.value)
        settingsData.value = newSettings
    }
}