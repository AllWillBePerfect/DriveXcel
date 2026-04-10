package org.my.drivexcel.domain.exception

import org.my.drivexcel.base.domain.exception.DomainException

class StorageDomainException(cause: Throwable? = null, message: String? = null) : DomainException(cause, message)