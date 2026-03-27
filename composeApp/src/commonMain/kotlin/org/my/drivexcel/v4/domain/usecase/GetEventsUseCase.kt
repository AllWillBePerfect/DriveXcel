package org.my.drivexcel.v4.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.my.drivexcel.v4.base.domain.usecase.IOUseCase
import org.my.drivexcel.v4.base.domain.usecase.v2.BaseFlowUseCase
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.repository.EventRepository
import org.my.drivexcel.v4.domain.repository.GetEventsRepository

class GetEventsUseCase(
    private val repo: EventRepository
): BaseFlowUseCase<Unit, List<EventDomainModel>>() {
    override fun execute(request: Unit): Flow<List<EventDomainModel>> {
        return repo.getEventsFlow()
    }
}