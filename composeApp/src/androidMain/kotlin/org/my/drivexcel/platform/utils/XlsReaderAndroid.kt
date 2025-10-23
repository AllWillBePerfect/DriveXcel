package org.my.drivexcel.platform.utils

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class XlsReaderAndroid(
    private val context: Context
) : XlsReader() {
    override fun openInput(path: String): InputStream =
        FileInputStream(File(context.filesDir, path))

    override fun openOutput(path: String): OutputStream =
        FileOutputStream(File(context.filesDir, path))
}