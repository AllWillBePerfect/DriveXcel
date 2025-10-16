package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface SettingsDataSource {
    val userSettings: Flow<UserSettings>
    suspend fun update(transform: (UserSettings) -> UserSettings)
}

@Serializable
data class UserSettings(
    val nightMode: NightMode
) {
    @Serializable
    enum class NightMode {
        YES, NO, FOLLOW_SYSTEM
    }

    companion object {
        fun createDefault() = UserSettings(
            nightMode = NightMode.FOLLOW_SYSTEM
        )
    }
}