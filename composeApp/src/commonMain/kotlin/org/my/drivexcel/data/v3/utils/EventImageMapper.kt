package org.my.drivexcel.data.v3.utils

import org.my.drivexcel.domain.models.EventImage
import org.my.drivexcel.domain.models.ImageExtension

class EventImageMapper {

    fun fromBytes(bytes: ByteArray): EventImage {
        val extension = detectExtension(bytes)
        return EventImage(bytes, extension)
    }

    fun toBytes(image: EventImage): ByteArray {
        return image.bytes
    }

    private fun detectExtension(bytes: ByteArray): ImageExtension {
        if (bytes.size < 4) {
            throw IllegalArgumentException("Invalid image data")
        }

        return when {
            bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() ->
                ImageExtension.JPEG

            bytes[0] == 0x89.toByte()
                    && bytes[1] == 0x50.toByte()
                    && bytes[2] == 0x4E.toByte()
                    && bytes[3] == 0x47.toByte() ->
                ImageExtension.PNG

            else -> throw IllegalArgumentException("Unsupported image format")
        }
    }
}