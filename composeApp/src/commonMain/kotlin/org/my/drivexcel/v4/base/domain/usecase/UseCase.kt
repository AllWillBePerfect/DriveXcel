package org.my.drivexcel.v4.base.domain.usecase

interface UseCase<T, R> {
    fun execute(input: T, onResult: (R) -> Unit)
}