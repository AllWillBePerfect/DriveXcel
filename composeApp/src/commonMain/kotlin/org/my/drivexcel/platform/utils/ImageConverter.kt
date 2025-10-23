package org.my.drivexcel.platform.utils

import androidx.compose.ui.graphics.ImageBitmap

interface ImageConverter {
    fun byteArrayToImageBitmap(bytes: ByteArray): ImageBitmap
}