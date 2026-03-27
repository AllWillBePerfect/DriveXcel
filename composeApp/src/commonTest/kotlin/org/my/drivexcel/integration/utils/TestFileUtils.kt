package org.my.drivexcel.integration.utils

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

object TestFileUtils {

    fun getFilesAndDirs(rootDir: Path, id: String): List<String> =
        Files.list(rootDir.resolve(id)).use { stream ->
            stream.map { it.fileName.toString() }.collect(Collectors.toList())
        }
}