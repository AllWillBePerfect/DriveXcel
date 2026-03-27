package org.my.drivexcel.v4.domain.repository

import kotlinx.coroutines.flow.Flow
import org.my.drivexcel.v4.domain.model.LeaderUserDomainModel

class GetLeaderUsersUseCase(
    private val repo: EventRepository
) {

    operator fun invoke(id: String): Flow<List<LeaderUserDomainModel>> {
        return repo.getLeaderUsersFlow(id)
    }
}