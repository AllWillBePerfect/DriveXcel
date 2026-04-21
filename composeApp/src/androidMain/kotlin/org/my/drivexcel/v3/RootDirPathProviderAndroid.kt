package org.my.drivexcel.v3

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.my.drivexcel.data.models.toPath
import org.my.drivexcel.datasource.sources.PreferencesDataSource
import org.my.drivexcel.platform.RootDirPathProvider
import java.nio.file.Path

class RootDirPathProviderAndroid(
    private val context: Context,
    private val preferencesDataSource: PreferencesDataSource
) : RootDirPathProvider {
    override fun provide(): Path {
        return runBlocking {
            val dirTypePath = preferencesDataSource.dirTypeFlow.first().toPath()
            val dir = context.filesDir.resolve(dirTypePath)
            dir.toPath()
        }
    }
}