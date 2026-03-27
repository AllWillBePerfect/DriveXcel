package org.my.drivexcel.v4.domain.usecase

import org.my.drivexcel.v4.base.domain.ext.DomainResult
import org.my.drivexcel.v4.base.domain.ext.runCatchingDomainResult
import org.my.drivexcel.v4.domain.repository.DeleteEventRepository
import org.my.drivexcel.v4.domain.repository.EventRepository

class DeleteEventUseCase(
    private val repo: EventRepository
) {

    suspend operator fun invoke(id: String): DomainResult<Unit> {
        return runCatchingDomainResult {
            repo.deleteEvent(id)
        }
    }
}