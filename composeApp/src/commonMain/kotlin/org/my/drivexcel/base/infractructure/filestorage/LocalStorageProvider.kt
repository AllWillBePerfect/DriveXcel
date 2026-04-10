package org.my.drivexcel.base.infractructure.filestorage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.my.drivexcel.datasource.exception.StorageDataException
import org.my.drivexcel.platform.RootDirPathProvider
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors
import kotlin.io.path.isDirectory

class LocalStorageProvider(
    pathProvider: RootDirPathProvider
) : StorageProvider {

    private val rootDir = pathProvider.provide()

    init {
        if (Files.exists(rootDir) && !Files.isDirectory(rootDir)) {
            throw StorageDataException.RootPathExistsButNotADirDataException(rootDir.toString())
        }
        runSafely { Files.createDirectories(rootDir) }
    }

    override suspend fun createDirectory(relativePathToDir: String) {
        withContext(Dispatchers.IO) {
            if (relativePathToDir.isBlank()) {
                throw StorageDataException.EmptyPathToDirDataException(relativePathToDir)
            }
            val fullPathToDir = resolveSecure(relativePathToDir)
            runSafely { Files.createDirectories(fullPathToDir) }
        }
    }

    override suspend fun deleteDirectory(relativePathToDir: String) {
        withContext(Dispatchers.IO) {
            val fullPathToDir = resolveSecure(relativePathToDir)

            if (relativePathToDir.isBlank()) {
                throw StorageDataException.EmptyPathToDirDataException(relativePathToDir)
            }

            if (!Files.exists(fullPathToDir)) {
                throw StorageDataException.DirDoesNotExistsDataException(fullPathToDir.toString())
            }

            runSafely {
                Files.walk(fullPathToDir).use {
                    it.sorted(Comparator.reverseOrder())
                        .forEach(Files::deleteIfExists)
                }
            }

        }
    }

    override suspend fun writeFile(relativePathToFile: String, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            if (relativePathToFile.isBlank()) {
                throw StorageDataException.InvalidPathDataException(relativePathToFile)
            }
            val fullPathToFile = resolveSecure(relativePathToFile)
            val fullPathToSubDir = fullPathToFile.parent

            if (fullPathToSubDir == rootDir) {
                throw StorageDataException.RootDirDataException(fullPathToSubDir.toString())
            }

            if (!Files.isDirectory(fullPathToSubDir)) {
                throw StorageDataException.DirDoesNotExistsDataException(fullPathToFile.toString())
            }

            runSafely {
                Files.write(
                    fullPathToFile,
                    bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            }

        }
    }

    override suspend fun deleteFile(relativePathToFile: String) {
        withContext(Dispatchers.IO) {
            val fullPathToFile = resolveSecure(relativePathToFile)

            if (fullPathToFile == rootDir) {
                throw StorageDataException.RootDirDataException(fullPathToFile.toString())
            }

            if (Files.isDirectory(fullPathToFile)) {
                throw StorageDataException.PathIsDirDataException(fullPathToFile.toString())
            }

            runSafely { Files.deleteIfExists(fullPathToFile) }
        }
    }

    override suspend fun readFile(relativePathToFile: String): ByteArray {
        return withContext(Dispatchers.IO) {
            val fullPathToFile = resolveSecure(relativePathToFile)

            if (fullPathToFile == rootDir) {
                throw StorageDataException.RootDirDataException(fullPathToFile.toString())
            }

            if (!Files.exists(fullPathToFile)) {
                throw StorageDataException.FileNotExistsDataException(fullPathToFile.toString())
            }

            if (Files.isDirectory(fullPathToFile)) {
                throw StorageDataException.PathIsDirDataException(fullPathToFile.toString())
            }
            runSafely { Files.readAllBytes(fullPathToFile) }
        }
    }

    override suspend fun exists(relativePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val fullPath = resolveSecure(relativePath)
            Files.exists(fullPath)
        }
    }

    override suspend fun deleteFiles(relativePath: String, pattern: String) {
        withContext(Dispatchers.IO) {
            val pathToDir = resolveSecure(relativePath)

            if (pathToDir == rootDir) {
                throw StorageDataException.RootDirDataException(pathToDir.toString())
            }

            runSafely {
                Files.newDirectoryStream(pathToDir, pattern).use { stream ->
                    stream.forEach { path ->
                        if (!path.isDirectory()) {
                            Files.deleteIfExists(path)
                        }
                    }
                }
            }
        }
    }

    override suspend fun getDirFilesAndDirs(relativePath: String): List<String> {
        return withContext(Dispatchers.IO) {
            val pathToDir = resolveSecure(relativePath)
            if (!Files.exists(pathToDir)) {
                throw StorageDataException.DirDoesNotExistsDataException(pathToDir.toString())
            }
            Files.list(pathToDir).use { stream ->
                stream.map { it.fileName.toString() }.collect(Collectors.toList())
            }
        }
    }

    override suspend fun getDirFilesAndDirsWithRootAllowed(relativePath: String): List<String> {
        return withContext(Dispatchers.IO) {
            val pathToDir = resolveSecure(relativePath, true)
            if (!Files.exists(pathToDir)) {
                throw StorageDataException.DirDoesNotExistsDataException(pathToDir.toString())
            }
            Files.list(pathToDir).use { stream ->
                stream.map { it.fileName.toString() }.collect(Collectors.toList())
            }
        }
    }

    private fun resolveSecure(path: String, rootDirAllowed: Boolean = false): Path {
        val resolved = rootDir.resolve(path).normalize()

        if (!resolved.startsWith(rootDir)) {
            throw StorageDataException.PathTraversalDataException(path)
        }

        if (!rootDirAllowed && resolved == rootDir) {
            throw StorageDataException.RootDirDataException(resolved.toString())
        }

        return resolved
    }

    private fun <T> runSafely(block: () -> T): T {
        return try {
            block()
        } catch (e: IOException) {
            throw StorageDataException.IODataException(e)
        }
    }
}