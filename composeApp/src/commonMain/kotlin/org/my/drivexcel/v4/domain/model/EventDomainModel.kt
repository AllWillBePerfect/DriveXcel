package org.my.drivexcel.v4.domain.model

data class EventDomainModel(
    val id: String,
    val name: String,
    val byteArray: ByteArray?
)