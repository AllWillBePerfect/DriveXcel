package org.my.drivexcel.mock.unit.domain.usecase

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.repository.GetEventsRepository
import org.my.drivexcel.v4.domain.usecase.GetEventsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class GetEventsUseCaseTest {

    private val repo = mockk<GetEventsRepository>()
    private lateinit var classUnderTest: GetEventsUseCase


    @Before
    fun setUp() {
        classUnderTest = GetEventsUseCase(repo)
    }

    /*@Test
    fun `Given events When invoked Then emits events`() = runTest {
        // Given
        val expectedEvents = listOf(EventDomainModel("1", "A"))
        every { repo.getEventsFlow() } returns flowOf(expectedEvents)

        // When
        val actual = classUnderTest(Unit).first()

        // Then
        assertEquals(expectedEvents, actual)
    }*/
}