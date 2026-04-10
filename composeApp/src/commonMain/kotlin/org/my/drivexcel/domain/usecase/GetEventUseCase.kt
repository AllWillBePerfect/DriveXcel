package org.my.drivexcel.domain.usecase

import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.wrapper.DomainResult
import org.my.drivexcel.base.domain.ext.runCatchingDomainResult

class GetEventUseCase(
    private val repo: EventRepository
) {
    suspend operator fun invoke(id: String): DomainResult<EventDomainModel> =
        runCatchingDomainResult { repo.getEvent(id) }
}