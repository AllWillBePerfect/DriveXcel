package org.my.drivexcel.platform.utils

import java.io.File

class DirectoryPathProviderJvm : DirectoryPathProvider {

    override fun provideHomePathFile(): File {
        return File(
            System.getProperty(AppLogger.JVM_FOLDER_DIRECTORY),
            AppLogger.JVM_FOLDER_DIRECTORY_NAME
        )
    }

}