package org.my.drivexcel.base.infractructure.filestorage


interface StorageProvider {
    suspend fun createDirectory(relativePathToDir: String)
    suspend fun deleteDirectory(relativePathToDir: String)

    suspend fun writeFile(relativePathToFile: String, bytes: ByteArray)
    suspend fun deleteFile(relativePathToFile: String)
    suspend fun readFile(relativePathToFile: String): ByteArray
    suspend fun exists(relativePath: String): Boolean

    suspend fun deleteFiles(relativePath: String, pattern: String)
    suspend fun getDirFilesAndDirs(relativePath: String): List<String>
    suspend fun getDirFilesAndDirsWithRootAllowed(relativePath: String): List<String>
}

