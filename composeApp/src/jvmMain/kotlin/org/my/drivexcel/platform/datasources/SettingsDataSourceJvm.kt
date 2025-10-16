package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class SettingsDataSourceJvm(
) : SettingsDataSource {

    private val filePath: Path = Paths.get(System.getProperty("user.home"), ".myapp_settings.json")

    private val stateFlow = MutableStateFlow(load())

    override val userSettings: Flow<UserSettings>
        get() = stateFlow.asStateFlow()

    override suspend fun update(transform: (UserSettings) -> UserSettings) = withContext(Dispatchers.IO) {
        val newSettings = transform(stateFlow.value)
        save(newSettings)
        stateFlow.value = newSettings
    }

    private fun load(): UserSettings {
        return try {
            val text = Files.readString(filePath)
            Json.Default.decodeFromString(UserSettings.serializer(), text)
        } catch (e: Exception) {
            UserSettings.createDefault()
        }
    }

    private fun save(settings: UserSettings) {
        Files.writeString(
            filePath,
            Json.Default.encodeToString(UserSettings.serializer(), settings),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}