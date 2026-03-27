package org.my.drivexcel.data.v3

import java.util.UUID

interface FolderIdProvider {

    fun generateId(): String

    class UUIDImpl : FolderIdProvider {
        override fun generateId(): String {
            return UUID.randomUUID().toString()
        }

    }
}