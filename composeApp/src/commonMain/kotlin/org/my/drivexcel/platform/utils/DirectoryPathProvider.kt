package org.my.drivexcel.platform.utils

import java.io.File

interface DirectoryPathProvider {

    fun provideHomePathFile(): File
}