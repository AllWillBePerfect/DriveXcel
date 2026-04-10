package org.my.drivexcel.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface SerializableParser {
    fun <T> toByteArray(
        value: T,
        serializer: KSerializer<T>
    ): ByteArray

    fun <T> fromByteArray(
        byteArray: ByteArray,
        serializer: KSerializer<T>
    ): T

    class Impl(
        private val json: Json
    ) : SerializableParser {

        override fun <T> toByteArray(
            value: T,
            serializer: KSerializer<T>
        ): ByteArray {
            return json.encodeToString(serializer, value)
                .encodeToByteArray()
        }

        override fun <T> fromByteArray(
            byteArray: ByteArray,
            serializer: KSerializer<T>
        ): T {
            val string = byteArray.decodeToString()
            return json.decodeFromString(serializer, string)
        }

    }
}