package org.my.drivexcel.integration.infrastructure

/*import org.my.drivexcel.v4.datasource.exception.DirDoesNotExistsDataException
import org.my.drivexcel.v4.datasource.exception.EmptyPathToDirDataException
import org.my.drivexcel.v4.datasource.exception.FileNotExistsDataException
import org.my.drivexcel.v4.datasource.exception.InvalidPathDataException
import org.my.drivexcel.v4.datasource.exception.PathIsDirDataException
import org.my.drivexcel.v4.datasource.exception.PathTraversalDataException
import org.my.drivexcel.v4.datasource.exception.RootDirDataException
import org.my.drivexcel.v4.datasource.exception.RootPathExistsButNotADirDataException*/
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.my.drivexcel.data.v3.RootDirPathProvider
import org.my.drivexcel.v4.base.infractructure.filestorage.LocalStorageProvider
import org.my.drivexcel.v4.base.infractructure.filestorage.StorageProvider
import org.my.drivexcel.v4.datasource.exception.StorageDataException.DirDoesNotExistsDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.EmptyPathToDirDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.FileNotExistsDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.InvalidPathDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.PathIsDirDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.PathTraversalDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.RootDirDataException
import org.my.drivexcel.v4.datasource.exception.StorageDataException.RootPathExistsButNotADirDataException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StorageProviderTest {

    private lateinit var rootDir: Path
    private lateinit var pathProvider: RootDirPathProvider
    private lateinit var classUnderTest: StorageProvider

    @get:Rule
    val tempFolder = TemporaryFolder()

    @BeforeTest
    fun setUp() {
        rootDir = tempFolder.newFolder().toPath()
        pathProvider = object : RootDirPathProvider {
            override fun provide(): Path = rootDir
        }
        classUnderTest = LocalStorageProvider(pathProvider)
    }


    /*@After
    fun tearDown() {
        Files.walk(rootDir).use {
            it.sorted(Comparator.reverseOrder())
                .forEach(Files::deleteIfExists)
        }
    }*/


    @Test
    fun `Given file path to root dir When PathProvider creates Then throw RootPathExistsButNotADirDataException`() {
        // Given
        val tempFile = tempFolder.newFile("file.txt").toPath()

        // When
        val badPathProvider = object : RootDirPathProvider {
            override fun provide(): Path = tempFile
        }

        //Then
        assertFailsWith<RootPathExistsButNotADirDataException> {
            LocalStorageProvider(badPathProvider)
        }
    }

    @Test
    fun `Given empty path When createDirectory called Then throw EmptyPathToDirDataException`() =
        runTest {
            assertFailsWith<EmptyPathToDirDataException> {
                classUnderTest.createDirectory("")
            }
        }

    @Test
    fun `Given dir path When createDirectory Then correct create dir`() = runTest {
        // Given
        val dirPath = "dir1"
        // When
        classUnderTest.createDirectory(dirPath)
        // Then
        assertTrue { classUnderTest.exists(dirPath) }

    }

    @Test
    fun `Given existing dir path When deleteDirectory Then correct delete dir`() = runTest {
        // Given
        val dirPath = "dir1"
        // When
        classUnderTest.createDirectory(dirPath)
        classUnderTest.deleteDirectory(dirPath)
        // Then

        assertFalse { classUnderTest.exists(dirPath) }
    }

    @Test
    fun `Given path to root path When deleteDirectory Then throw RootDirDataException`() =
        runTest {
            // Given
            val dirPath = ""

            // When
            // Then
            assertFailsWith<RootDirDataException> {
                classUnderTest.deleteDirectory(dirPath)
            }

        }

    @Test
    fun `Given path to not existing dir When deleteDirectory Then throw RootDirDataException`() =
        runTest {
            // Given
            val dirPath = "dir"
            val subDirPath = "$dirPath/dir2"

            // When
            classUnderTest.createDirectory(dirPath)
            // Then
            assertFailsWith<DirDoesNotExistsDataException> {
                classUnderTest.deleteDirectory(subDirPath)
            }

        }

    @Test
    fun `Given not existing dir path When deleteDirectory Then throw DirDoesNotExistsDataException`() =
        runTest {
            // Given
            val dirPath = "dir1"

            // When
            // Then
            assertFailsWith<DirDoesNotExistsDataException> {
                classUnderTest.deleteDirectory(dirPath)
            }

        }

    @Test
    fun `Given path to file in existing dir When writeBytes Then correct write file`() = runTest {
        // Given
        val dirPath = "dir1"
        val filePath = "$dirPath/file.txt"
        val fileBytes = ByteArray(10)

        // When
        classUnderTest.createDirectory(dirPath)
        classUnderTest.writeFile(filePath, fileBytes)

        // Then
        assertTrue { classUnderTest.exists(dirPath) }
        assertTrue { Files.isDirectory(rootDir.resolve(dirPath)) }
        assertTrue { classUnderTest.exists(filePath) }
        assertTrue { !Files.isDirectory(rootDir.resolve(filePath)) }

    }

    @Test
    fun `Given path to file When writeFile twice with different files Then overwrites existing file`() =
        runTest {
            // Given
            val dirPath = "dir"
            val filePath = "$dirPath/file1.txt"
            val oldFileBytes = ByteArray(10) { it.toByte() }
            val expectedFileBytes = ByteArray(22) { it.toByte() }

            // When
            classUnderTest.createDirectory(dirPath)
            classUnderTest.writeFile(filePath, oldFileBytes)
            classUnderTest.writeFile(filePath, expectedFileBytes)

            val actualFileBytes = classUnderTest.readFile(filePath)

            // Then
            assertTrue { actualFileBytes.contentEquals(expectedFileBytes) }
        }

    @Test
    fun `Given empty path When writeFile called Then throw InvalidPathDataException`() = runTest {

        //Given
        val bytes = ByteArray(10)

        // When
        // Then
        assertFailsWith<InvalidPathDataException> {
            classUnderTest.writeFile("", bytes)
        }
    }

    @Test
    fun `Given file path to root dir When writeFile Then throw WriteFileToRootDirDataException`() =
        runTest {
            // Given
            val filePath = "file.txt"
            val fileBytes = ByteArray(10)

            // When
            // Then
            assertFailsWith<RootDirDataException> {
                classUnderTest.writeFile(filePath, fileBytes)
            }
        }

    @Test
    fun `Given file path to not existing dir When writeFile Then throw DirDoesNotExistsDataException`() =
        runTest {
            // Given
            val filePath = "dir1/file.txt"
            val fileBytes = ByteArray(10)

            // When
            // Then
            assertFailsWith<DirDoesNotExistsDataException> {
                classUnderTest.writeFile(filePath, fileBytes)
            }
        }

    @Test
    fun `Given traversal path When writeFile Then throw PathTraversalDataException`() = runTest {
        // Given
        val invalidPath = listOf(
            "../file.txt",
        )
        val fileBytes = ByteArray(10)

        // When
        // Then
        invalidPath.forEach {
            assertFailsWith<PathTraversalDataException> {
                classUnderTest.writeFile(it, fileBytes)
            }
        }

    }

    @Test
    fun `Given invalid path to file When writeFile Then throw DirDoesNotExistsDataException`() =
        runTest {
            // Given
            val invalidPaths = listOf(
                "dir/file.",
                "dir/file",
            )
            val fileBytes = ByteArray(10)

            // When
            // Then
            invalidPaths.forEach {
                assertFailsWith<DirDoesNotExistsDataException> {
                    classUnderTest.writeFile(it, fileBytes)
                }
            }


        }

    @Test
    fun `Given invalid path When writeFile Then throw RootDirDataException`() = runTest {
        val fileBytes = ByteArray(10)

        val invalidPaths = listOf(
            ".",
            "dir/..",
//            "dir/..sdd",
        )

        invalidPaths.forEach { path ->
            assertFailsWith<RootDirDataException> {
                classUnderTest.writeFile(path, fileBytes)
            }
        }
    }

    @Test
    fun `Given path to file When deleteFile Then correct delete file`() = runTest {
        // Given
        val dirPath = "dir"
        val filePath = "$dirPath/file.txt"
        val fileBytes = ByteArray(10)
        // When
        classUnderTest.createDirectory(dirPath)
        classUnderTest.writeFile(filePath, fileBytes)
        classUnderTest.deleteFile(filePath)
        // Then
        assertFalse { classUnderTest.exists(filePath) }

    }

    @Test
    fun `Given path to dir When deleteFile Then throw PathIsDirDataException`() = runTest {
        // Given
        val dirPath = "dir1/subDir1"

        // When
        classUnderTest.createDirectory(dirPath)

        // Then
        assertFailsWith<PathIsDirDataException> {
            classUnderTest.deleteFile(dirPath)

        }
    }

    @Test
    fun `Given root dir When deleteFile Then throw RootDirDataException`() = runTest {
        // Given
        val rootDir = ""

        // When
        // Then
        assertFailsWith<RootDirDataException> {
            classUnderTest.deleteFile(rootDir)
        }

    }

    @Test
    fun `Given file When writeFile Then correct write file`() = runTest {
        // Given
        val dirPath = "dir"
        val filePath = "$dirPath/file.txt"
        val expectedFileBytes = ByteArray(10) { it.toByte() }

        // When
        classUnderTest.createDirectory(dirPath)
        classUnderTest.writeFile(filePath, expectedFileBytes)
        val actualFileBytes = classUnderTest.readFile(filePath)

        // Then
        assertTrue { expectedFileBytes.contentEquals(actualFileBytes) }
    }

    @Test
    fun `Given root dir When readFile Then throw RootDirDataException`() = runTest {
        assertFailsWith<RootDirDataException> {
            classUnderTest.readFile("")
        }
    }

    @Test
    fun `Given not existing path to file When readFile Then throw FileNotExistsDataException`() =
        runTest {
            assertFailsWith<FileNotExistsDataException> {
                classUnderTest.readFile("dir/file.txt")
            }
        }

    @Test
    fun `Given path to existing dir When readFile Then throw PathIsDirDataException`() = runTest {
        val dirPath = "dir/file"
        classUnderTest.createDirectory(dirPath)
        assertFailsWith<PathIsDirDataException> {
            classUnderTest.readFile(dirPath)
        }
    }

    @Test
    fun `Given dir with files and sub dir When deleteFiles Then correct delete only matching pattern`() =
        runTest {
            // Given
            val dirPath = "dir"
            val subDirPath = "$dirPath/image"
            val image1Path = "$dirPath/image1.png"
            val image2Path = "$dirPath/image2.jpg"
            val image3Path = "$dirPath/image3.webm"
            val filePath = "$dirPath/file.txt"
            val image1Bytes = ByteArray(10) { it.toByte() }
            val image2Bytes = ByteArray(14) { it.toByte() }
            val image3Bytes = ByteArray(120) { it.toByte() }
            val fileBytes = ByteArray(60) { it.toByte() }

            // When
            classUnderTest.createDirectory(dirPath)
            classUnderTest.createDirectory(subDirPath)
            classUnderTest.writeFile(image1Path, image1Bytes)
            classUnderTest.writeFile(image2Path, image2Bytes)
            classUnderTest.writeFile(image3Path, image3Bytes)
            classUnderTest.writeFile(filePath, fileBytes)

            classUnderTest.deleteFiles(dirPath, "image*")

            val list = Files.list(rootDir.resolve(dirPath)).use { stream ->
                stream.map { path -> path.fileName.toString() }.collect(
                    Collectors.toList()
                )
            }

            // Then
            assertEquals(setOf("image", "file.txt"), list.toSet())

        }

    @Test
    fun `Given files in dir and sub dir When deleteFiles Then should not delete files inside subdirectories`() =
        runTest {
            // Given
            val dirPath = "dir"
            val subDirPath = "$dirPath/image"

            classUnderTest.createDirectory(dirPath)
            classUnderTest.createDirectory(subDirPath)

            classUnderTest.writeFile("$dirPath/image1.png", ByteArray(10))
            classUnderTest.writeFile("$subDirPath/image2.png", ByteArray(10))

            // When
            classUnderTest.deleteFiles(dirPath, "image*")

            // Then
            val rootList = Files.list(rootDir.resolve(dirPath)).use {
                it.map { p -> p.fileName.toString() }.collect(Collectors.toList())
            }

            val subList = Files.list(rootDir.resolve(subDirPath)).use {
                it.map { p -> p.fileName.toString() }.collect(Collectors.toList())
            }

            assertEquals(setOf("image"), rootList.toSet())
            assertEquals(setOf("image2.png"), subList.toSet())
        }

    @Test
    fun `Given empty dir When deleteFiles Then complete without exception`() = runTest {
        // Given
        val dirPath = "dir"

        // When
        classUnderTest.createDirectory(dirPath)
        classUnderTest.deleteFiles(dirPath, "image*")


        // Then
        val list = Files.list(rootDir.resolve(dirPath)).use {
            it.collect(Collectors.toList())
        }

        assertTrue(list.isEmpty())
    }

    @Test
    fun `Given dir with one file When getDirFilesAndDirs Then return list with one file`() =
        runTest {
            // Given
            val dirPath = "dir"
            val filePath = "$dirPath/file.txt"
            // When
            classUnderTest.createDirectory(dirPath)
            classUnderTest.writeFile(filePath, ByteArray(10) { it.toByte() })
            val list = classUnderTest.getDirFilesAndDirs(dirPath)

            // Then
            assertEquals(setOf("file.txt"), list.toSet())
        }

    @Test
    fun `Given dir with one file and one sub dir When getDirFilesAndDirs Then return list with one file and one sub dir`() =
        runTest {
            // Given
            val dirPath = "dir"
            val filePath = "$dirPath/file.txt"
            val subDirPath = "$dirPath/sub"
            // When
            classUnderTest.createDirectory(dirPath)
            classUnderTest.createDirectory(subDirPath)
            classUnderTest.writeFile(filePath, ByteArray(10) { it.toByte() })
            val list = classUnderTest.getDirFilesAndDirs(dirPath)

            // Then
            assertEquals(setOf("file.txt", "sub"), list.toSet())
        }

    @Test
    fun `Given not existing dir When getDirFilesAndDirs Then throw DirDoesNotExistsDataException`() =
        runTest {
            assertFailsWith<DirDoesNotExistsDataException> {
                classUnderTest.getDirFilesAndDirs("dir/sub")
            }
        }

    @Test
    fun `Given When Then`() = runTest {
        // Given
        val dirPath = "dir1"

        // When
        classUnderTest.createDirectory(dirPath)
        val dirs = classUnderTest.getDirFilesAndDirsWithRootAllowed("")
        // Then
        assertEquals(setOf("dir1"), dirs.toSet())
    }

}