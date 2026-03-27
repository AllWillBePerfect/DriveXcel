package org.my.drivexcel.v4.datasource.exception.mapper

import kotlinx.coroutines.CancellationException
import org.my.drivexcel.v4.base.domain.exception.DomainException
import org.my.drivexcel.v4.base.domain.exception.UnknownDomainException
import org.my.drivexcel.v4.datasource.exception.StorageDataException
import org.my.drivexcel.v4.domain.exception.StorageDomainException

class DataExceptionToDomainMapper {

    fun toDomain(e: Exception): DomainException {
        e.rethrowIfFatal()

        return when (e) {
            is StorageDataException ->
                StorageDomainException(e)

            else ->
                UnknownDomainException(e)
        }
    }
}

fun Exception.rethrowIfFatal() {
    if (this is CancellationException) throw this
}
