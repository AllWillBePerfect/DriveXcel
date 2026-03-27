package org.my.drivexcel.v3

import android.content.Context
import org.my.drivexcel.data.v3.RootDirPathProvider
import org.my.drivexcel.platform.utils.AppLogger
import java.nio.file.Path

class RootDirPathProviderAndroid(
    private val context: Context
) : RootDirPathProvider {
    override fun provide(): Path {
        val dir = context.filesDir.resolve(AppLogger.EVENTS_FOLDER)
        return dir.toPath()
    }
}