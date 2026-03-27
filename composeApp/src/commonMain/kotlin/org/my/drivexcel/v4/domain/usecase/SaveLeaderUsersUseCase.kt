package org.my.drivexcel.v4.domain.usecase

import org.my.drivexcel.v4.base.domain.ext.DomainResult
import org.my.drivexcel.v4.base.domain.ext.runCatchingDomainResult
import org.my.drivexcel.v4.domain.repository.EventRepository

class SaveLeaderUsersUseCase(
    private val repo: EventRepository
) {
    suspend operator fun invoke(id: String, xlsList: List<ByteArray>): DomainResult<Unit> =
        runCatchingDomainResult {
            repo.mergeXls(id, xlsList)
        }
}