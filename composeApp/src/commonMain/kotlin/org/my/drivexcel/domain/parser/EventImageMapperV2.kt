package org.my.drivexcel.domain.parser

import org.my.drivexcel.domain.model.EventImageDomainModel
import org.my.drivexcel.domain.model.ImageExtension

class EventImageMapperV2 {

    fun fromByteArray(bytes: ByteArray): EventImageDomainModel {
        val extension = detectExtension(bytes)

        return EventImageDomainModel(
            bytes = bytes,
            extension = extension
        )
    }

    fun toByteArray(image: EventImageDomainModel): ByteArray {
        return image.bytes
    }

    private fun detectExtension(bytes: ByteArray): ImageExtension {
        return when {
            bytes.startsWith(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)) ->
                ImageExtension.PNG

            bytes.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte())) ->
                ImageExtension.JPEG

            bytes.startsWith(byteArrayOf(0x52, 0x49, 0x46, 0x46)) ->
                ImageExtension.WEBP

            else -> error("Unsupported image format")
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (size < prefix.size) return false
        for (i in prefix.indices) {
            if (this[i] != prefix[i]) return false
        }
        return true
    }
}