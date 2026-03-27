package org.my.drivexcel.mock.unit.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.my.drivexcel.v4.domain.exception.CreateEventDomainException
import org.my.drivexcel.v4.domain.model.EventDomainModel
import org.my.drivexcel.v4.domain.usecase.CreateEventRepository
import org.my.drivexcel.v4.domain.usecase.CreateEventUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateEventUseCaseTest {

    private lateinit var classUnderTest: CreateEventUseCase

    @MockK
    private lateinit var createEventRepository: CreateEventRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        classUnderTest = CreateEventUseCase(createEventRepository)
    }

    /*@Test
    fun `Given valid name and not existing When invoke Then return created event`() = runTest {
        // Given
        val expectedEvent = EventDomainModel(id = "1", name = "hello")
        coEvery { createEventRepository.nameExists("hello") } returns false
        coEvery { createEventRepository.create("hello") } returns expectedEvent

        // When
        val actualEvent = classUnderTest("hello")

        // Then
        assertEquals(expectedEvent, actualEvent)
        // Проверяет порядок выполнения методов
        coVerifyOrder {
            createEventRepository.nameExists("hello")
            createEventRepository.create("hello")
        }

    }

    @Test
    fun `Given blank name When invoke Then throw InvalidName`() = runTest {
        assertFailsWith<CreateEventDomainException.EmptyName> {
            classUnderTest("")
        }


        coVerify(exactly = 0) { createEventRepository.nameExists(any()) }
        coVerify(exactly = 0) { createEventRepository.create(any()) }
    }

    @Test
    fun `Given existing name When invoke Then throw NameAlreadyExists`() = runTest {
        coEvery { createEventRepository.nameExists("hello") } returns true

        assertFailsWith<CreateEventDomainException.NameAlreadyExists> {
            classUnderTest("hello")
        }
        coVerify { createEventRepository.nameExists("hello") }
        coVerify(exactly = 0) { createEventRepository.create(any()) }
    }*/




}