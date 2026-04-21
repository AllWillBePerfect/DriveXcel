package org.my.drivexcel.domain.demo

import org.my.drivexcel.base.domain.wrapper.DomainEitherResult
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.domain.usecase.CreateEventUseCase
import org.my.drivexcel.domain.usecase.SaveLeaderUsersUseCase

class EventDemoDataGenerator(
    private val repository: EventRepository,
    private val createEventUseCase: CreateEventUseCase,
    private val saveLeaderUsersUseCase: SaveLeaderUsersUseCase
) {
    suspend fun generateTestData() {
        repository.deleteAllEvents()
        val createEventResult = createEventUseCase("first event", null)
        if (createEventResult is DomainEitherResult.Success)
            saveLeaderUsersUseCase.invokeFromUsers(
                createEventResult.data.id,
                LeaderUserDomainModel.generateUsers(100)
            )

        val createEventResult2 = createEventUseCase("second event", null)
        if (createEventResult2 is DomainEitherResult.Success)
            saveLeaderUsersUseCase.invokeFromUsers(
                createEventResult2.data.id,
                LeaderUserDomainModel.generateUsers(100)
            )

        val createEventResult3 = createEventUseCase("third event", null)
        if (createEventResult3 is DomainEitherResult.Success)
            saveLeaderUsersUseCase.invokeFromUsers(
                createEventResult3.data.id,
                LeaderUserDomainModel.generateUsers(100)
            )


    }
}