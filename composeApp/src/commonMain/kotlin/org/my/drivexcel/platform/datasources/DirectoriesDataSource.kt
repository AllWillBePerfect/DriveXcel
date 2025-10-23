package org.my.drivexcel.platform.datasources

import kotlinx.coroutines.flow.Flow

interface DirectoriesDataSource {

    val events: Flow<List<EventDirectory>>

    fun createEventDirectory(eventName: String, imageBytes: ByteArray? = null)

    fun findFirstExcelFilePath(): String?

    fun findExcelFilePathByEventName(eventName: String): String?


}

data class EventDirectory(
    val eventName: String,
    val image: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventDirectory

        if (eventName != other.eventName) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventName.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}