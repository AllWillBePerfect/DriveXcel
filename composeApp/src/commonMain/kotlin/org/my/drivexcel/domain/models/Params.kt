package org.my.drivexcel.domain.models

data class CreateEventParams(
    val eventName: String,
    val image: ByteArray? = null
)