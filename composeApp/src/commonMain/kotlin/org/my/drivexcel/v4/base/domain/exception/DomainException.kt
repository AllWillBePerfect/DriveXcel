package org.my.drivexcel.v4.base.domain.exception

abstract class DomainException(cause: Throwable? = null, message: String? = null) : Exception(message, cause)

sealed class DomainExceptionn(e: Exception? = null, message: String? = null) : Exception(message, e) {
    class NetworkException(e: Exception? = null, message: String? = null) : DomainExceptionn(e, message)
    class DataBaseException(e: Exception? = null, message: String? = null) : DomainExceptionn(e, message)
    class UnknownException(e: Exception? = null, message: String? = null) : DomainExceptionn(e, message)
}
