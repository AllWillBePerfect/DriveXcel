package org.my.drivexcel.v4.base.domain.usecase.v2

interface SuspendUseCase<in T, out R> {
    suspend operator fun invoke(input: T): R
}