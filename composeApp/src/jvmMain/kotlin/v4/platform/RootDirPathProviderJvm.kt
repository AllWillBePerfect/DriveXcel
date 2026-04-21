package v4.platform

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.my.drivexcel.data.models.toPath
import org.my.drivexcel.datasource.sources.PreferencesDataSource
import org.my.drivexcel.platform.AppLogger
import org.my.drivexcel.platform.RootDirPathProvider
import java.nio.file.Path
import java.nio.file.Paths

class RootDirPathProviderJvm(
    private val preferencesDataSource: PreferencesDataSource
) : RootDirPathProvider {
    override fun provide(): Path {
        return runBlocking {
            try {
                val dirTypePath = preferencesDataSource.dirTypeFlow.first().toPath()
                val userHome = System.getProperty(AppLogger.Companion.JVM_FOLDER_DIRECTORY)
                val subPath = AppLogger.Companion.JVM_FOLDER_DIRECTORY_NAME
                val eventsFolder = dirTypePath
                Paths.get(userHome, subPath, eventsFolder)
            } catch (e: Exception) {
                println("EXCEPTION CAUSE: $e")
                throw e
            }
        }
    }
}