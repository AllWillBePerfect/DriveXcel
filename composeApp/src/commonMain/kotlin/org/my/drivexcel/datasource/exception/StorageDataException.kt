package org.my.drivexcel.datasource.exception

import org.my.drivexcel.base.datasource.exception.DataException
import java.io.IOException

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

    data class IODataException(val e: IOException) : StorageDataException(cause = e)

    data class PathIsDirDataException(val path: String) :
        StorageDataException(message = "path: $path")

    data class PathTraversalDataException(val path: String) : StorageDataException(message = path)

    data class RootDirDataException(val path: String) :
        StorageDataException(message = "path: $path")

    data class RootPathExistsButNotADirDataException(val rootDir: String) :
        StorageDataException(message = rootDir)

}