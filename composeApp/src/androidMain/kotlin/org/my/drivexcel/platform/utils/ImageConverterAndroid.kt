package org.my.drivexcel.platform.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

class ImageConverterAndroid : ImageConverter {
    override fun byteArrayToImageBitmap(bytes: ByteArray): ImageBitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()

    }
}