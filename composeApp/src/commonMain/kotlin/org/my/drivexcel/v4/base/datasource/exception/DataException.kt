package org.my.drivexcel.v4.base.datasource.exception

abstract class DataException(
    cause: Throwable? = null,
    message: String? = null,
) : Exception(message, cause)