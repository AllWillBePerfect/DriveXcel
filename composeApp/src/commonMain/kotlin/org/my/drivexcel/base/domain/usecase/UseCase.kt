package org.my.drivexcel.base.domain.usecase

interface UseCase<T, R> {
    fun execute(input: T, onResult: (R) -> Unit)
}