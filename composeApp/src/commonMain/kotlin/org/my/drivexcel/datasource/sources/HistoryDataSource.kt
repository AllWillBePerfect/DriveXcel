package org.my.drivexcel.datasource.sources

import kotlinx.coroutines.withContext
import org.my.drivexcel.base.domain.dispatcher.AppDispatchers
import org.my.drivexcel.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.data.SerializableParser
import org.my.drivexcel.data.models.HistoryDataModel
import org.my.drivexcel.datasource.exception.StorageDataException

interface HistoryDataSource {

    suspend fun readHistory(dirId: String): HistoryDataModel?
    suspend fun writeHistory(dirId: String, history: HistoryDataModel)

    class Impl(
        private val storage: StorageProvider,
        private val parser: SerializableParser,
        private val dispatchers: AppDispatchers
    ) : HistoryDataSource {
        override suspend fun readHistory(dirId: String): HistoryDataModel? =
            withContext(dispatchers.io) {
                val path = buildPath(dirId)
                val bytes = try {
                    storage.readFile(path)
                } catch (e: StorageDataException.FileNotExistsDataException) {
                    return@withContext null
                }
                return@withContext parser.fromByteArray(bytes, HistoryDataModel.serializer())
            }


        override suspend fun writeHistory(
            dirId: String,
            history: HistoryDataModel
        ) = withContext(dispatchers.io) {
            val bytes = parser.toByteArray(history, HistoryDataModel.serializer())
            storage.writeFile(buildPath(dirId), bytes)
        }

        private fun buildPath(dirId: String) = "$dirId/history.json"


    }
}