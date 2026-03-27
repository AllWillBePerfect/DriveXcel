package org.my.drivexcel

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.my.drivexcel.data.v3.EventFileStore
import org.my.drivexcel.data.v3.EventPathResolver
import org.my.drivexcel.data.v3.FolderIdProvider
import org.my.drivexcel.data.v3.TimeProvider
import org.my.drivexcel.data.v3.repositories.EventRepositoryImpl
import org.my.drivexcel.domain.models.EventId
import org.my.drivexcel.domain.models.EventImage
import org.my.drivexcel.domain.models.EventMeta
import org.my.drivexcel.domain.models.ImageExtension
import org.my.drivexcel.domain.repositories.EventRepository
import org.my.drivexcel.fake.EventFileStoreFake
import org.my.drivexcel.fake.ExcelParserFake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/*
class EventRepositoryTest {

    private lateinit var json: Json
    private lateinit var storage: EventFileStoreFake
    private lateinit var repo: EventRepository
    private lateinit var timeProvider: TimeProvider

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun setup() {

        val folderIdProvider = object : FolderIdProvider {
            var id = 1
            override fun generateId(): String {
                return "event${id++}"
            }
        }

        timeProvider = object : TimeProvider {
           override fun now(): Instant {
               return Instant.parse("2026-01-01T00:00:00Z")

           }
       }
        json = Json
        storage = EventFileStoreFake()
        val excelParser = ExcelParserFake()
        repo = EventRepositoryImpl(
            folderIdProvider = folderIdProvider,
            pathResolver = EventPathResolver.Impl(),
            timeProvider = timeProvider,
            json = json,
            storage = storage,
            excelParser = excelParser
        )
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateEvent replaces old background`() = runTest {

        repo.createEvent(
            name = "name",
            image = null
        )
        storage.writeFile("event1/background.png", ByteArray(1))

        repo.updateEvent(
            id = EventId("event1"),
            name = "New",
            image = EventImage(
                bytes = ByteArray(2),
                extension = ImageExtension.JPEG
            )
        )

        assertFalse(storage.fileExists("event1/background.png"))
        assertTrue(storage.fileExists("event1/background.jpg"))
    }

    @Test
    fun `updateEvent removes background when image is null`() = runTest {

        repo.createEvent(
            name = "name",
            image = EventImage(
                ByteArray(1024),
                ImageExtension.JPEG
            )
        )

        assertTrue(storage.fileExists("event1/background.jpg"))

        repo.updateEvent(
            id = EventId("event1"),
            name = "New",
            image = null
        )

        val metaBytes = storage.readFile("event1/meta.json")
        val meta = json.decodeFromString<EventMeta>(metaBytes.decodeToString())

        assertFalse(storage.fileExists("event1/background.png"))
        assertEquals("New", meta.name)
    }


    @Test
    fun `updateEvent with null image and no background does nothing`() = runTest {

        repo.createEvent("name", null)

        repo.updateEvent(
            id = EventId("event1"),
            name = "New",
            image = null
        )

        assertTrue(
            storage.listFilesInEventDir("event1")
                .none { it.startsWith("background.") }
        )
    }

}*/
