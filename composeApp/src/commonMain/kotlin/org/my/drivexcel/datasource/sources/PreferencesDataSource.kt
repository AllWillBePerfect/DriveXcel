package org.my.drivexcel.datasource.sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.my.drivexcel.base.domain.dispatcher.AppDispatchers
import org.my.drivexcel.base.domain.model.NightModeModel
import org.my.drivexcel.data.models.DirTypeDataModel

interface PreferencesDataSource {

    val nightModeModelFlow: Flow<NightModeModel>
    val userAuthorizedFlow: Flow<Boolean>
    val dirTypeFlow: Flow<DirTypeDataModel>

    suspend fun switchNightMode(nightModeModel: NightModeModel)
    suspend fun authorizeUser()
    suspend fun unauthorizeUser()
    suspend fun setLocalDirType()
    suspend fun setTestDirType()

    class Impl(
        private val dataStore: DataStore<Preferences>,
        private val dispatchers: AppDispatchers

    ) : PreferencesDataSource {

        override val nightModeModelFlow: Flow<NightModeModel> = dataStore.data.map { preferences ->
            try {
                val token = preferences[NIGHT_MODE_MODEL_KEY] ?: NightModeModel.FOLLOW_SYSTEM.name
                NightModeModel.valueOf(token)
            } catch (e: IllegalArgumentException) {
                NightModeModel.FOLLOW_SYSTEM
            }
        }

        override val userAuthorizedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
            preferences[USER_AUTHORIZED_KEY] ?: false
        }

        override val dirTypeFlow: Flow<DirTypeDataModel> = dataStore.data.map { preferences ->
            try {
                val token = preferences[DIR_TYPE_KEY] ?: DirTypeDataModel.LOCAL.name
                DirTypeDataModel.valueOf(token)
            } catch (e: IllegalArgumentException) {
                DirTypeDataModel.LOCAL
            }
        }

        override suspend fun switchNightMode(nightModeModel: NightModeModel) {
            withContext(dispatchers.io) {
                dataStore.edit { preferences ->
                    preferences[NIGHT_MODE_MODEL_KEY] = nightModeModel.name
                }
            }
        }

        override suspend fun authorizeUser() {
            withContext(dispatchers.io) {
                dataStore.edit { preferences ->
                    preferences[USER_AUTHORIZED_KEY] = true
                }
            }
        }

        override suspend fun unauthorizeUser() {
            withContext(dispatchers.io) {
                dataStore.edit { preferences ->
                    preferences[USER_AUTHORIZED_KEY] = false
                }
            }
        }

        override suspend fun setLocalDirType() {
            withContext(dispatchers.io) {
                dataStore.edit { preferences ->
                    preferences[DIR_TYPE_KEY] = DirTypeDataModel.LOCAL.name
                }
            }
        }

        override suspend fun setTestDirType() {
            withContext(dispatchers.io) {
                dataStore.edit { preferences ->
                    preferences[DIR_TYPE_KEY] = DirTypeDataModel.TEST.name
                }
            }
        }

        companion object {
            private val NIGHT_MODE_MODEL_KEY = stringPreferencesKey("night_mode")
            private val USER_AUTHORIZED_KEY = booleanPreferencesKey("user_authorized")
            private val DIR_TYPE_KEY = stringPreferencesKey("dir_type")
        }
    }


}