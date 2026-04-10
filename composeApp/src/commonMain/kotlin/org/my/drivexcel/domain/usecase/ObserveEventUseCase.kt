package org.my.drivexcel.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.wrapper.FlowResult
import org.my.drivexcel.base.domain.ext.toFlowResult

class ObserveEventUseCase(
    private val repo: EventRepository
) {

    operator fun invoke(eventId: String): Flow<FlowResult<EventDomainModel>> {
        return repo.observeEvent(eventId).toFlowResult()
    }

}