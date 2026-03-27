package org.my.drivexcel.v4.datasource.exception

import org.my.drivexcel.v4.base.datasource.exception.DataException

sealed class StorageDataException(
    message: String? = null,
    cause: Throwable? = null
) : DataException(
    cause,
    message
) {
    data class DirDoesNotExistsDataException(val path: String) :
        StorageDataException(message = path)

    data class EmptyPathToDirDataException(val path: String) : StorageDataException(message = path)

    data class FileNotExistsDataException(val path: String) : StorageDataException(message = path)

    data class InvalidPathDataException(val path: String) : StorageDataException()

    data class IODataException(val e: java.io.IOException) : StorageDataException(cause = e)

    data class PathIsDirDataException(val path: String) :
        StorageDataException(message = "path: $path")

    data class PathTraversalDataException(val path: String) : StorageDataException(message = path)

    data class RootDirDataException(val path: String) :
        StorageDataException(message = "path: $path")

    data class RootPathExistsButNotADirDataException(val rootDir: String) :
        StorageDataException(message = rootDir)

}