package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.flow.Flow

interface DirectoriesDataSource {

    val events: Flow<List<EventDirectory>>

    suspend fun createEventDirectory(eventName: String, imageBytes: ByteArray? = null)

    suspend fun findFirstExcelFilePath(): String?

    suspend fun findExcelFilePathByEventName(eventName: String): String?

}

data class EventDirectory(
    val id: String,
    val name: String,
    val imagePath: String?
)