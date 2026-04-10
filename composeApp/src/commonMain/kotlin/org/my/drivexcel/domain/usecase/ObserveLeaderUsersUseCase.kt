package org.my.drivexcel.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.repository.EventRepository

class ObserveLeaderUsersUseCase(
    private val repo: EventRepository
) {

    operator fun invoke(id: String): Flow<List<LeaderUserDomainModel>> {
        return repo.observeUsers(id)
    }
}