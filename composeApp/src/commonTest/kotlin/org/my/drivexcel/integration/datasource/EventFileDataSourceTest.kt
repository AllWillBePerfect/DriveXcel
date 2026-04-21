package org.my.drivexcel.integration.datasource

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.my.drivexcel.platform.FolderIdProvider
import org.my.drivexcel.platform.RootDirPathProvider
import org.my.drivexcel.domain.model.EventImageDomainModel
import org.my.drivexcel.domain.model.ImageExtension
import org.my.drivexcel.integration.utils.TestFileUtils.getFilesAndDirs
import org.my.drivexcel.infrastructure.LocalStorageProvider
import org.my.drivexcel.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.data.SerializableParser
import org.my.drivexcel.data.models.MetaDataModel
import org.my.drivexcel.datasource.sources.EventFileDataSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventFileDataSourceTest {

    private lateinit var rootDir: Path
    private lateinit var storageProvider: StorageProvider
    private lateinit var classUnderTest: EventFileDataSource

    @get:Rule
    val tempFolder = TemporaryFolder()

    @BeforeTest
    fun setUp() {
        rootDir = tempFolder.newFolder().toPath()
        val pathProvider = object : RootDirPathProvider {
            override fun provide(): Path = rootDir
        }
        storageProvider = LocalStorageProvider(
            pathProvider = pathProvider
        )
        val folderIdProvider = object : FolderIdProvider {
            var id = 1
            override fun generateId(): String {
                return "event${id++}"
            }
        }
        classUnderTest = EventFileDataSource.Impl(
            storageProvider = storageProvider,
            folderIdProvider = folderIdProvider,
            serializableParser = SerializableParser.Impl(Json)
        )
    }

    @Test
    fun `Given file name When createEvent Then creates meta file`() = runTest {
        //Given
        val eventName = "first event"

        //When
        val id = classUnderTest.createEvent(eventName, null)
        val files = getFilesAndDirs(rootDir, id)

        val metaBytes = storageProvider.readFile("$id/meta.json")
        val meta = Json.decodeFromString<MetaDataModel>(metaBytes.decodeToString())

        //Then
        assertEquals(setOf("meta.json"), files.toSet())
        assertEquals(eventName, meta.eventName)
        assertNull(meta.imageExtension)
        assertTrue { Files.exists(rootDir.resolve(id)) }
    }

    @Test
    fun `Given file name and image When createEvent Then creates meta file with image`() = runTest {
        //Given
        val eventName = "first event"
        val expectedImageBytes = ByteArray(10) {it.toByte()}
        val image = EventImageDomainModel(expectedImageBytes, ImageExtension.PNG)

        //When
        val id = classUnderTest.createEvent(eventName, image)
        val files = getFilesAndDirs(rootDir, id)

        val metaBytes = storageProvider.readFile("$id/meta.json")
        val meta = Json.decodeFromString<MetaDataModel>(metaBytes.decodeToString())

        val actualImageBytes = storageProvider.readFile("$id/image.png")

        //Then
        assertEquals(setOf("meta.json", "image.png"), files.toSet())
        assertEquals(eventName, meta.eventName)
        assertEquals(ImageExtension.PNG, meta.imageExtension)

        assertTrue { Files.exists(rootDir.resolve(id)) }
        assertTrue { actualImageBytes.contentEquals(expectedImageBytes) }

    }

    @Test
    fun `Given multiple events When getEvents Then returns all`() = runTest {

        classUnderTest.createEvent("event1", null)
        classUnderTest.createEvent("event2", null)

        val events = classUnderTest.getEvents()

        assertEquals(2, events.size)
    }

    @Test
    fun `Given JPG image When createEvent Then saves jpg image`() = runTest {
        val imageBytes = ByteArray(5) { it.toByte() }
        val image = EventImageDomainModel(imageBytes, ImageExtension.JPEG)

        val id = classUnderTest.createEvent("event", image)

        val files = getFilesAndDirs(rootDir, id)

        assertTrue(files.contains("image.jpg"))
    }

    @Test
    fun `Given PNG image When updateEvent with JPG Then replaces image`() = runTest {

        val pngImage = EventImageDomainModel(ByteArray(5) {1}, ImageExtension.PNG)
        val id = classUnderTest.createEvent("event", pngImage)

        val jpgImage = EventImageDomainModel(ByteArray(5) {2}, ImageExtension.JPEG)

        classUnderTest.updateEvent(id, "event", jpgImage)

        val files = getFilesAndDirs(rootDir, id)

        assertFalse(files.contains("image.png"))
        assertTrue(files.contains("image.jpg"))
    }


}

@RunWith(Parameterized::class)
class SomeTest(
    private val imageExtension: ImageExtension
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testData() = ImageExtension.entries
    }

    private lateinit var rootDir: Path
    private lateinit var storageProvider: StorageProvider
    private lateinit var classUnderTest: EventFileDataSource

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Before
    fun setUp() {
        rootDir = tempFolder.newFolder().toPath()
        val pathProvider = object : RootDirPathProvider {
            override fun provide(): Path = rootDir
        }
        storageProvider = LocalStorageProvider(
            pathProvider = pathProvider
        )
        val folderIdProvider = object : FolderIdProvider {
            var id = 1
            override fun generateId(): String {
                return "event${id++}"
            }
        }
        classUnderTest = EventFileDataSource.Impl(
            storageProvider = storageProvider,
            folderIdProvider = folderIdProvider,
            serializableParser = SerializableParser.Impl(Json)
        )
    }

    @Test
    fun `Given JPG image When createEvent Then saves jpg image`() = runTest {
        val imageBytes = ByteArray(5) { it.toByte() }
        val image = EventImageDomainModel(imageBytes, imageExtension)

        val id = classUnderTest.createEvent("event", image)

        val files = getFilesAndDirs(rootDir, id)

        assertTrue(files.contains("image.${image.extension.value}"))
    }

    @Test
    fun `Given image When createEvent Then saves image with correct extension`() = runTest {

        val imageBytes = ByteArray(5) { it.toByte() }
        val image = EventImageDomainModel(imageBytes, imageExtension)

        val id = classUnderTest.createEvent("event", image)

        val files = getFilesAndDirs(rootDir, id)

        assertTrue(files.contains("image.${imageExtension.value}"))

        val metaBytes = storageProvider.readFile("$id/meta.json")
        val meta = Json.decodeFromString<MetaDataModel>(metaBytes.decodeToString())

        assertEquals(imageExtension, meta.imageExtension)

        val savedImage = storageProvider.readFile("$id/image.${imageExtension.value}")

        assertTrue(savedImage.contentEquals(imageBytes))
    }


}