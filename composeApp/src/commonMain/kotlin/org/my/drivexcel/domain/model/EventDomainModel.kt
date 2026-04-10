package org.my.drivexcel.domain.model

data class EventDomainModel(
    val id: String,
    val name: String,
    val byteArray: ByteArray?
) {
    companion object {
        fun empty(): EventDomainModel = EventDomainModel(
            id = "42",
            name = "empty",
            byteArray = null
        )
    }
}