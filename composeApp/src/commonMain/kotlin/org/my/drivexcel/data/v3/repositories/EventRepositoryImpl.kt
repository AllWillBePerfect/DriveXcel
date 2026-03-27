package org.my.drivexcel.data.v3.repositories

import kotlinx.serialization.json.Json
import org.my.drivexcel.data.v3.EventFileStore
import org.my.drivexcel.data.v3.EventPathResolver
import org.my.drivexcel.data.v3.ExcelParser
import org.my.drivexcel.data.v3.FolderIdProvider
import org.my.drivexcel.data.v3.TimeProvider
import org.my.drivexcel.domain.models.Event
import org.my.drivexcel.domain.models.EventAbsolute
import org.my.drivexcel.domain.models.EventId
import org.my.drivexcel.domain.models.EventImage
import org.my.drivexcel.domain.models.EventMeta
import org.my.drivexcel.domain.models.ImageSource
import org.my.drivexcel.domain.models.Participant
import org.my.drivexcel.domain.repositories.EventRepository
import kotlin.time.ExperimentalTime

class EventRepositoryImpl(
    private val folderIdProvider: FolderIdProvider,
    private val pathResolver: EventPathResolver,
    private val timeProvider: TimeProvider,
    private val json: Json,
    private val storage: EventFileStore,
    private val excelParser: ExcelParser,
) : EventRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun createEvent(name: String, image: EventImage?) {
        val eventId = folderIdProvider.generateId()

        try {
//            storage.createDirectory(eventId)

            image?.let {
                storage.writeFile(
                    pathResolver.backgroundPath(
                        eventId = eventId,
                        extension = it.extension
                    ),
                    it.bytes
                )
            }

            val instant = timeProvider.now()

            val meta = EventMeta(
                name = name,
                createdAt = instant,
                updatedAt = instant
            )
            val metaRaw = json.encodeToString(meta)

            storage.writeFile(
                pathResolver.metaPath(eventId),
                metaRaw.encodeToByteArray()
            )

        } catch (e: Exception) {
            storage.deleteDirectory(eventId)
            throw e
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun updateEvent(
        id: EventId,
        name: String,
        image: EventImage?
    ) {

        val metaPath = pathResolver.metaPath(id.value)

        if (!storage.fileExists(metaPath)) {
            return
        }

        val metaBytes = storage.readFile(pathResolver.metaPath(id.value))
        val meta = json.decodeFromString<EventMeta>(metaBytes.decodeToString())

        val redactedMeta = meta.copy(
            name = name,
            updatedAt = timeProvider.now()
        )

        val existingFiles = storage.listFilesInEventDir(id.value)
        existingFiles
            .filter { it.startsWith("background.") }
            .forEach {
                storage.deleteFile("${id.value}/$it")
            }

        image?.let {
            storage.writeFile(
                pathResolver.backgroundPath(id.value, it.extension),
                it.bytes
            )
        }

        storage.writeFile(metaPath, json.encodeToString(redactedMeta).encodeToByteArray())
    }

    override suspend fun deleteEvent(id: EventId) {
        storage.deleteDirectory(id.value)
    }

    override suspend fun getAllEvents(): List<Event> {
        val ids = storage.listDirectories()

        return ids.mapNotNull { id ->
            val metaPath = pathResolver.metaPath(id)

            if (!storage.fileExists(metaPath)) {
                return@mapNotNull null
            }

            val imagePath = findImageRelativePath(id)

            val metaBytes = storage.readFile(metaPath)
            val meta = json.decodeFromString<EventMeta>(metaBytes.decodeToString())

            Event(
                id = EventId(id),
                imageRelativePath = imagePath,
                meta = meta
            )
        }

    }

    override suspend fun getAllEventsAbsolute(): List<EventAbsolute> {
        val ids = storage.listDirectories()

        return ids.mapNotNull { id ->
            val metaPath = pathResolver.metaPath(id)

            if (!storage.fileExists(metaPath)) {
                return@mapNotNull null
            }

            val imagePath = findImageRelativePath(id)

            val metaBytes = storage.readFile(metaPath)
            val meta = json.decodeFromString<EventMeta>(metaBytes.decodeToString())

            val absoluteImagePath = imagePath?.let { storage.resolveToAbsolutePath(it) }

            EventAbsolute(
                id = EventId(id),
                imageAbsolutePath = absoluteImagePath,
                meta = meta
            )
        }
    }

    override suspend fun getParticipants(id: EventId): List<Participant> {
        val excelPath = pathResolver.participantsPath(id.value)

        val existingParticipants = if (storage.fileExists(excelPath)) {
            val excelBytes = storage.readFile(excelPath)
            excelParser.parse(excelBytes)
        } else {
            emptyList()
        }

        return existingParticipants
    }

    override suspend fun mergeParticipantsExcel(
        id: EventId,
        files: List<ByteArray>
    ) {
        val path = pathResolver.participantsPath(id.value)

        val existingParticipants = if (storage.fileExists(path)) {
            val excelBytes = storage.readFile(path)
            excelParser.parse(excelBytes)
        } else {
            emptyList()
        }

        val newParticipants = files.flatMap { bytes -> excelParser.parse(bytes) }

        val merged = (existingParticipants + newParticipants).associateBy { it.id }
            .values
            .toList()

        val mergedBytes = excelParser.write(merged)

        storage.writeFile(path, mergedBytes)
    }

    private suspend fun findImageRelativePath(id: String): String? {

        val files = storage.listFilesInEventDir(id)

        return files
            .firstOrNull { it.startsWith("background.") }
            ?.let { "$id/$it" }
    }

}