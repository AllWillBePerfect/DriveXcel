package org.my.drivexcel

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.my.drivexcel.data.v3.EventFileStore
import org.my.drivexcel.data.v3.RootDirPathProvider
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails

/*
class EventFileStorageTest {

    private lateinit var tempDir: java.nio.file.Path
    private lateinit var pathProvider: RootDirPathProvider
    private lateinit var storage: EventFileStore.Impl

    @BeforeTest
    fun setup() {
        tempDir = Files.createDirectories(
            Paths.get(
                System.getProperty("user.home"),
                "event-storage-test"
            )
        )
        pathProvider = object : RootDirPathProvider {
            override fun provide(): java.nio.file.Path {
                return tempDir
            }
        }
        println("Temp dir: ${tempDir.toAbsolutePath()}")
        storage = EventFileStore.Impl(pathProvider)

    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).use { paths ->
            paths.sorted(Comparator.reverseOrder())
                .forEach { Files.deleteIfExists(it) }
        }
    }

    @Test
    fun `root directory is created on init`() {
        assertTrue(Files.exists(tempDir))
    }

    @Test
    fun `create and delete directory`() = runTest {
        storage.createDirectory("event1")

        assertTrue(Files.exists(tempDir.resolve("event1")))

        storage.deleteDirectory("event1")

        assertFalse(Files.exists(tempDir.resolve("event1")))
    }

    @Test
    fun `create 2 directories`() = runTest {
        storage.createDirectory("event1")
        storage.createDirectory("event2")

        val folders = mutableSetOf<String>()

        Files.list(tempDir).use { paths ->
            paths.forEach { path ->
                folders.add(path.fileName.toString())
            }
        }

        assertEquals(setOf("event1", "event2"), folders)
    }

    @Test
    fun `write and read file`() = runTest {
        val path = "event1/meta.json"
        val data = "hello world".encodeToByteArray()

        storage.writeFile(path, data)

        assertTrue(storage.fileExists(path))

        val result = storage.readFile(path)

        assertEquals("hello world", result.decodeToString())
    }

    @Test
    fun `write several times and read file with last record`() = runTest {
        val path = "event1/meta.json"
        val data1 = "hello, world".encodeToByteArray()
        val data2 = "hello, kotlin".encodeToByteArray()

        storage.writeFile(path, data1)

        assertTrue(storage.fileExists(path))

        val result1 = storage.readFile(path)

        assertEquals("hello, world", result1.decodeToString())

        storage.writeFile(path, data2)

        val result2 = storage.readFile(path)

        assertEquals("hello, kotlin", result2.decodeToString())

    }

    @Test
    fun `delete directory removes nested files`() = runTest {
        storage.writeFile("event1/meta.json", "test".encodeToByteArray())

        storage.deleteDirectory("event1")

        assertFalse(Files.exists(tempDir.resolve("event1")))
    }

    @Test
    fun `delete one directory does not affect others`() = runTest {
        storage.writeFile("event1/meta1.json", "a".encodeToByteArray())
        storage.writeFile("event2/meta2.json", "b".encodeToByteArray())

        storage.deleteDirectory("event1")

        assertFalse(Files.exists(tempDir.resolve("event1")))

        assertTrue(Files.exists(tempDir.resolve("event2")))

        assertTrue(Files.exists(tempDir.resolve("event2/meta2.json")))
    }

    @Test
    fun `delete non existing directory does not crash`() = runTest {
        storage.deleteDirectory("not-existing")

        assertTrue(Files.exists(tempDir))
    }

    @Test
    fun `fileExists returns false for missing file`() = runTest {
        assertFalse(storage.fileExists("event1/meta.json"))
    }

    @Test
    fun `writeFile creates nested directories`() = runTest {
        storage.writeFile("event1/sub/inner/meta.json", "x".encodeToByteArray())

        assertTrue(
            Files.exists(tempDir.resolve("event1/sub/inner/meta.json"))
        )
    }

    @Test
    fun `writeFile should not allow path traversal`() = runTest {
        assertFails {
            storage.writeFile("../hack.txt", "x".encodeToByteArray())
        }
    }

    @Test
    fun `deleteDirectory should not allow escaping root`() = runTest {
        assertFails {
            storage.deleteDirectory("../")
        }
    }

    @Test
    fun `write and read image file`() = runTest {

        val imagePath = "event1/background.png"

        val imageBytes = ByteArray(1024) { it.toByte() }

        storage.writeFile(imagePath, imageBytes)

        assertTrue(storage.fileExists(imagePath))

        val readBytes = storage.readFile(imagePath)

        assertContentEquals(imageBytes, readBytes)
    }

    @Test
    fun `listDirectories returns only directories`() = runTest {
        storage.createDirectory("event1")
        storage.createDirectory("event2")
        storage.writeFile("image.png", ByteArray(1024))

        val dirs = storage.listDirectories()

        assertEquals(listOf("event1", "event2"), dirs)
    }

    @Test
    fun `listDirectories returns empty list when none exist`() = runTest {
        val dirs = storage.listDirectories()
        assertTrue(dirs.isEmpty())
    }

    @Test
    fun `listFiles returns only files`() = runTest {

        storage.writeFile("event1/image.png", ByteArray(1024))
        storage.createDirectory("event1/event2")

        val files = storage.listFilesInEventDir("event1")

        assertEquals(listOf("image.png"), files)
    }

    @Test
    fun `listFiles returns empty list if directory does not exist`() = runTest {
        val files = storage.listFilesInEventDir("unknown")
        assertTrue(files.isEmpty())
    }



}*/
