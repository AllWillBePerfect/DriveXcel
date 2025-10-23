package org.my.drivexcel.platform.utils

import android.content.Context
import java.io.File

class DirectoryPathProviderAndroid(
    private val context: Context
) : DirectoryPathProvider {

    override fun provideHomePathFile(): File {
        return File(
            context.filesDir,
            AppLogger.JVM_FOLDER_DIRECTORY_NAME
        )
    }
}