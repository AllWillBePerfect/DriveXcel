package org.my.drivexcel.data

import java.io.File
import java.io.FileOutputStream

interface FileUtils {

    fun ensureDir(path: File)
    fun deleteDir(path: File): Boolean
    fun renameDir(from: File, to: File): Boolean
    fun deleteFile(path: File): Boolean

    fun writeBytes(file: File, bytes: ByteArray)
    fun writeText(file: File, text: String)
    fun readText(file: File): String?

    fun listSubDirs(dir: File): List<File>
    fun findFiles(dir: File, extensions: List<String>): List<File>
    fun findFirstFile(dir: File, extensions: List<String>): File?



    class Impl(
    ) : FileUtils {
        override fun ensureDir(path: File) {
            if (!path.exists()) path.mkdirs()
        }

        override fun deleteDir(path: File): Boolean =
            try {
                if (path.exists()) path.deleteRecursively() else true
            } catch (e: Exception) {
                false
            }

        override fun renameDir(from: File, to: File): Boolean =
            try {
                from.renameTo(to)
            } catch (e: Exception) {
                false
            }

        override fun deleteFile(path: File): Boolean =
            try { if (path.exists()) path.delete() else true } catch (e: Exception) { false }


        override fun writeBytes(file: File, bytes: ByteArray) {
//            file.parentFile?.mkdirs()
            FileOutputStream(file).use { it.write(bytes) }
        }

        override fun writeText(file: File, text: String) {
//            file.parentFile?.mkdirs()
            file.writeText(text)
        }

        override fun readText(file: File): String? =
            if (file.exists()) file.readText() else null


        override fun listSubDirs(dir: File): List<File> =
            dir.listFiles()?.filter { it.isDirectory } ?: emptyList()


        override fun findFiles(dir: File, extensions: List<String>): List<File> =
            dir.listFiles()?.filter { file ->
                extensions.any { ext -> file.name.lowercase().endsWith(ext) }
            } ?: emptyList()

        override fun findFirstFile(
            dir: File,
            extensions: List<String>
        ): File? =
            dir.listFiles()?.find { file ->
                extensions.any { ext -> file.name.lowercase().endsWith(ext) }
            }

    }
}