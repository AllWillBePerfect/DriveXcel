package org.my.drivexcel.v4.domain.exception

import org.my.drivexcel.v4.base.domain.exception.DomainException

class StorageDomainException(cause: Throwable? = null, message: String? = null) : DomainException(cause, message)