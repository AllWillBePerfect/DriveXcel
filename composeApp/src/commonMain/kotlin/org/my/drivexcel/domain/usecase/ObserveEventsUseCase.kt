package org.my.drivexcel.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.my.drivexcel.domain.model.EventDomainModel
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.usecase.v2.BaseFlowUseCase

class ObserveEventsUseCase(
    private val repo: EventRepository
): BaseFlowUseCase<Unit, List<EventDomainModel>>() {
    override fun execute(request: Unit): Flow<List<EventDomainModel>> {
        return repo.observeEvents()
    }
}