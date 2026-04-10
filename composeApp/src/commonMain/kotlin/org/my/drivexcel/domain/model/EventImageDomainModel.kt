package org.my.drivexcel.domain.model

data class EventImageDomainModel(
    val bytes: ByteArray,
    val extension: ImageExtension
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventImageDomainModel

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