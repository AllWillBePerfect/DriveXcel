package org.my.drivexcel.datasource.exception.mapper

import kotlinx.coroutines.CancellationException
import org.my.drivexcel.base.domain.exception.DomainException
import org.my.drivexcel.base.domain.exception.UnknownDomainException
import org.my.drivexcel.datasource.exception.StorageDataException
import org.my.drivexcel.domain.exception.StorageDomainException

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
