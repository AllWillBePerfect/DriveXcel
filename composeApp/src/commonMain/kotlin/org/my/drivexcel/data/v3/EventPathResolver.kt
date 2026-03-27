package org.my.drivexcel.data.v3

import org.my.drivexcel.domain.models.ImageExtension

interface EventPathResolver {

    fun metaPath(eventId: String): String

    fun participantsPath(eventId: String): String

    fun backgroundPath(
        eventId: String,
        extension: ImageExtension
    ): String

    class Impl() : EventPathResolver {

        override fun metaPath(eventId: String): String =
            "$eventId/meta.json"

        override fun participantsPath(eventId: String): String =
            "$eventId/participants.xlsx"

        override fun backgroundPath(
            eventId: String,
            extension: ImageExtension
        ): String =
            "$eventId/background.${extension.value}"

    }
}