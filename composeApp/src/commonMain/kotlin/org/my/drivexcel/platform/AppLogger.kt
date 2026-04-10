package org.my.drivexcel.platform

interface AppLogger {

    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)

    companion object {
        const val CONSOLE_LOGGER = "consoleLogger"
        const val CONSOLE_AND_LOCAL_FILE_LOGGER = "consoleAndLocalFileLogger"

        const val JVM_FOLDER_DIRECTORY = "user.home"
        const val JVM_FOLDER_DIRECTORY_NAME = ".driveXcel"
        const val JVM_LOGGER_FILE_NAME = "driveXcel-log.txt"
        const val EVENTS_FOLDER = "localEvents"

        const val LOCAL_ROOT_DIRECTORY = "local"
        const val GOOGLE_ROOT_DIRECTORY = "google"
        const val YANDEX_ROOT_DIRECTORY = "yandex"
    }


}