package org.my.drivexcel.v4.domain.usecase

import org.my.drivexcel.v4.base.domain.ext.DomainResult
import org.my.drivexcel.v4.base.domain.ext.runCatchingDomainResult
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.repository.EventRepository
import org.my.drivexcel.v4.domain.repository.GetEventRepository

class GetEventUseCase(
    private val repo: EventRepository
) {
    suspend operator fun invoke(id: String): DomainResult<EventDomainModel> =
        runCatchingDomainResult { repo.getEvent(id) }

}