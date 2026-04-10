package org.my.drivexcel.fake

class EventFileStoreFake : EventFileStore {
    val files = mutableMapOf<String, ByteArray>()
    override suspend fun createDirectory(id: String) {

    }

    override suspend fun deleteDirectory(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun writeFile(path: String, data: ByteArray) {
        files[path] = data
    }

    override suspend fun readFile(path: String): ByteArray {
        return files[path]
            ?: throw IllegalStateException("File not found: $path")
    }

    override suspend fun deleteFile(path: String) {
        files.remove(path)
    }

    override suspend fun fileExists(path: String): Boolean =
        files.containsKey(path)

    override suspend fun listDirectories(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun listFilesInEventDir(directoryId: String): List<String> =
        files.keys
            .filter { it.startsWith("$directoryId/") }
            .map { it.removePrefix("$directoryId/") }

    override fun resolveToAbsolutePath(path: String): String {
        TODO("Not yet implemented")
    }
}