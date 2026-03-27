package org.my.drivexcel.data.v3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists

interface EventFileStore {
    @Deprecated("Not needed")
    suspend fun createDirectory(id: String)
    suspend fun deleteDirectory(id: String)
    suspend fun writeFile(path: String, data: ByteArray)
    suspend fun readFile(path: String): ByteArray
    suspend fun fileExists(path: String): Boolean
    suspend fun listDirectories(): List<String>
    suspend fun listFilesInEventDir(directoryId: String): List<String>
    fun resolveToAbsolutePath(path: String): String
    suspend fun deleteFile(path: String)




    class Impl(
        private val pathProvider: RootDirPathProvider
    ) : EventFileStore {

        private val rootDir = pathProvider.provide()

        init {
            require(
                !Files.exists(rootDir) || Files.isDirectory(rootDir)
            ) { "Root path exists but is not a directory: $rootDir" }
            Files.createDirectories(rootDir)
        }

        override suspend fun createDirectory(id: String) {
            withContext(Dispatchers.IO) {
                val dir = rootDir.resolve(id)
                Files.createDirectories(dir)
            }
        }

        override suspend fun deleteDirectory(id: String) {
            withContext(Dispatchers.IO) {
                val dir = resolveSecure(id)
                if (Files.exists(dir)) {
                    Files.walk(dir).use { paths ->
                        paths.sorted(Comparator.reverseOrder())
                            .forEach { Files.deleteIfExists(it) }
                    }
                }
            }
        }


        override suspend fun writeFile(path: String, data: ByteArray) {
            withContext(Dispatchers.IO) {
                val fullPath = resolveSecure(path)
                Files.createDirectories(fullPath.parent)
                Files.write(
                    fullPath,
                    data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            }
        }

        override suspend fun readFile(path: String): ByteArray {
            return withContext(Dispatchers.IO) {
                val fullPath = rootDir.resolve(path)
                Files.readAllBytes(fullPath)
            }
        }

        override suspend fun fileExists(path: String): Boolean {
            return withContext(Dispatchers.IO) {
                resolveSecure(path).exists()
            }
        }

        override suspend fun listDirectories(): List<String> =
            withContext(Dispatchers.IO) {
                val result = mutableListOf<String>()

                Files.newDirectoryStream(rootDir).use { stream ->
                    for (path in stream) {
                        if (Files.isDirectory(path)) {
                            result.add(path.fileName.toString())
                        }
                    }
                }

                result
            }

        override suspend fun listFilesInEventDir(directoryId: String): List<String> =
            withContext(Dispatchers.IO) {

                val dir = resolveSecure(directoryId)

                if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                    return@withContext emptyList()
                }

                val result = mutableListOf<String>()

                Files.newDirectoryStream(dir).use { stream ->
                    for (path in stream) {
                        if (Files.isRegularFile(path)) {
                            result.add(path.fileName.toString())
                        }
                    }
                }

                result
            }

        override fun resolveToAbsolutePath(path: String): String {
            return rootDir.resolve(path).toAbsolutePath().toString()
        }

        override suspend fun deleteFile(path: String) =
            withContext(Dispatchers.IO) {

                val file = resolveSecure(path)

                if (Files.exists(file) && Files.isRegularFile(file)) {
                    Files.delete(file)
                }
            }


        private fun resolveSecure(path: String): Path {
            val resolved = rootDir.resolve(path).normalize()

            require(resolved.startsWith(rootDir)) {
                "Path traversal attempt: $path"
            }

            return resolved
        }
    }


}