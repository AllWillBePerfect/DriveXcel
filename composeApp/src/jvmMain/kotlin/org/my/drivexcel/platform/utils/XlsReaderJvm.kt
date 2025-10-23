package org.my.drivexcel.platform.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class XlsReaderJvm : XlsReader() {
    override fun openInput(path: String): InputStream =
        FileInputStream(File(path))

    override fun openOutput(path: String): OutputStream =
        FileOutputStream(File(path))
}