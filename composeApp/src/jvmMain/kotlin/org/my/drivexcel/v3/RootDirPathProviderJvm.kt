package org.my.drivexcel.v3

import org.my.drivexcel.data.v3.RootDirPathProvider
import org.my.drivexcel.platform.utils.AppLogger
import java.nio.file.Path
import java.nio.file.Paths

class RootDirPathProviderJvm: RootDirPathProvider {
    override fun provide(): Path {
        val userHome = System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY)
        val subPath = AppLogger.JVM_FOLDER_DIRECTORY_NAME
        val eventsFolder = AppLogger.EVENTS_FOLDER
        return Paths.get(userHome, subPath, eventsFolder)
    }
}