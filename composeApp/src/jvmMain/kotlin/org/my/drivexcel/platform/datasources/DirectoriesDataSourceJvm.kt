package org.my.drivexcel.platform.datasources

import org.my.drivexcel.platform.utils.AppLogger
import java.io.File
import java.nio.file.Paths

class DirectoriesDataSourceJvm : DirectoriesDataSource {

    private val eventsSubDir: File

    init {
        val eventDir =
            Paths.get(
                System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
                AppLogger.JVM_FOLDER_DIRECTORY_NAME
            ).toFile()
        if (!eventDir.exists()) {
            eventDir.mkdirs()
        }

        eventsSubDir = File(eventDir, AppLogger.JVM_EVENTS_ROOT_DIRECTORY).apply {
            if (!exists()) mkdirs()
        }
    }

    override fun createEventDirectory(eventName: String) {

    }

}