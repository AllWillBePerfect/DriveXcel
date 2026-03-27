package org.my.drivexcel.domain.models

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Event(
    val id: EventId,
    val imageRelativePath: String?,
    val meta: EventMeta
)

data class EventAbsolute(
    val id: EventId,
    val imageAbsolutePath: String?,
    val meta: EventMeta
)

@JvmInline
value class EventId(val value: String)

sealed interface ImageSource {
    data class Local(val path: String) : ImageSource
}

@Serializable
data class EventMeta @OptIn(ExperimentalTime::class) constructor(
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class EventImage(
    val bytes: ByteArray,
    val extension: ImageExtension
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventImage

        if (!bytes.contentEquals(other.bytes)) return false
        if (extension != other.extension) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + extension.hashCode()
        return result
    }
}

enum class ImageExtension(val value: String) {
    PNG("png"),
    JPEG("jpg"),
    WEBP("webp")
}

class EventImageMapperV2 {

    fun fromByteArray(bytes: ByteArray): EventImage {
        val extension = detectExtension(bytes)

        return EventImage(
            bytes = bytes,
            extension = extension
        )
    }

    fun toByteArray(image: EventImage): ByteArray {
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

