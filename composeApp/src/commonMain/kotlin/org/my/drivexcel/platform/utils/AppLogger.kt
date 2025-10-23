package org.my.drivexcel.platform.utils

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

        const val JVM_LOCAL_ROOT_DIRECTORY = "local"
        const val JVM_GOOGLE_ROOT_DIRECTORY = "google"
        const val JVM_YANDEX_ROOT_DIRECTORY = "yandex"
    }


}