package org.my.drivexcel.platform.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

class ImageConverterJvm : ImageConverter {
    override fun byteArrayToImageBitmap(bytes: ByteArray): ImageBitmap {
        return Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
}