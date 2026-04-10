package org.my.drivexcel.datasource.sources

import kotlinx.coroutines.withContext
import org.my.drivexcel.base.domain.dispatcher.AppDispatchers
import org.my.drivexcel.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.data.SerializableParser
import org.my.drivexcel.data.models.MetaDataModel
import org.my.drivexcel.datasource.exception.StorageDataException

interface EventMetaDataSource {

    suspend fun createEventDir(dirId: String)
    suspend fun deleteEventDir(dirId: String)
    suspend fun readMeta(dirId: String): MetaDataModel
    suspend fun writeMeta(dirId: String, meta: MetaDataModel)
    suspend fun readImage(dirId: String, extension: String): ByteArray?
    suspend fun writeImage(dirId: String, extension: String, bytes: ByteArray)
    suspend fun deleteImages(dirId: String)
    suspend fun getAllEventIds(): List<String>

    class Impl(
        private val storage: StorageProvider,
        private val parser: SerializableParser,
        private val dispatchers: AppDispatchers

    ) : EventMetaDataSource {
        override suspend fun createEventDir(dirId: String) = withContext(dispatchers.io) {
            storage.createDirectory(dirId)
        }

        override suspend fun deleteEventDir(dirId: String) = withContext(dispatchers.io) {
            storage.deleteDirectory(dirId)
        }

        override suspend fun readMeta(dirId: String): MetaDataModel = withContext(dispatchers.io) {
            val path = buildMetaPath(dirId)
            val bytes = storage.readFile(path)
            return@withContext parser.fromByteArray(bytes, MetaDataModel.serializer())
        }

        override suspend fun writeMeta(
            dirId: String,
            meta: MetaDataModel
        ) = withContext(dispatchers.io) {
            val bytes = parser.toByteArray(meta, MetaDataModel.serializer())
            storage.writeFile(buildMetaPath(dirId), bytes)
        }

        override suspend fun readImage(
            dirId: String,
            extension: String
        ): ByteArray? = withContext(dispatchers.io) {
            val path = buildImagePath(dirId, extension)
            return@withContext try {
                storage.readFile(path)
            } catch (e: StorageDataException.FileNotExistsDataException) {
                null
            }
        }

        override suspend fun writeImage(
            dirId: String,
            extension: String,
            bytes: ByteArray
        ) = withContext(dispatchers.io) {
            storage.writeFile(buildImagePath(dirId, extension), bytes)
        }

        override suspend fun deleteImages(dirId: String) = withContext(dispatchers.io) {
            storage.deleteFiles(dirId, IMAGE_PATTERN)
        }

        override suspend fun getAllEventIds(): List<String> = withContext(dispatchers.io) {
            return@withContext storage.getDirFilesAndDirsWithRootAllowed("")
        }

        private fun buildMetaPath(dirId: String) = "$dirId/meta.json"

        private fun buildImagePath(dirId: String, extension: String) =
            "$dirId/image.$extension"

        companion object {
            private const val IMAGE_PATTERN = "image*"
        }

    }
}