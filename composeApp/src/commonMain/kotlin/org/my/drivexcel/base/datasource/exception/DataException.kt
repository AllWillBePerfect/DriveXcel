package org.my.drivexcel.base.datasource.exception

abstract class DataException(
    cause: Throwable? = null,
    message: String? = null,
) : Exception(message, cause)