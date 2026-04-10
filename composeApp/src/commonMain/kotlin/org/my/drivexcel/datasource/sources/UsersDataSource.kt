package org.my.drivexcel.datasource.sources

import kotlinx.coroutines.withContext
import org.my.drivexcel.base.domain.dispatcher.AppDispatchers
import org.my.drivexcel.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.data.SerializableParser
import org.my.drivexcel.data.models.UsersDataModel
import org.my.drivexcel.datasource.exception.StorageDataException

interface UsersDataSource {

    suspend fun readUsers(dirId: String): UsersDataModel?
    suspend fun writeUsers(dirId: String, users: UsersDataModel)

    class Impl(
        private val storage: StorageProvider,
        private val parser: SerializableParser,
        private val dispatchers: AppDispatchers

    ) : UsersDataSource {

        override suspend fun readUsers(dirId: String): UsersDataModel? = withContext(dispatchers.io) {
            val path = buildPath(dirId)
            val bytes = try {
                storage.readFile(path)
            } catch (e: StorageDataException.FileNotExistsDataException) {
               return@withContext null
            }
            return@withContext parser.fromByteArray(bytes, UsersDataModel.serializer())
        }

        override suspend fun writeUsers(dirId: String, users: UsersDataModel) = withContext(dispatchers.io) {
            val bytes = parser.toByteArray(users, UsersDataModel.serializer())

            storage.writeFile(buildPath(dirId), bytes)
        }

        private fun buildPath(dirId: String) = "$dirId/users.json"

    }
}