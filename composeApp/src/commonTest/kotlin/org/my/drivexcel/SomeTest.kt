package org.my.drivexcel

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class SomeTest {

    private lateinit var classUnderTest: SomeUseCase

    @Mock
    private lateinit var repo: SomeRepository

    @Before
    fun setUp() {
        classUnderTest = SomeUseCase(repo)
    }

    @Test
    fun `given when then`() {
        // Given
        val expectedValue = true
        given { repo.getData() }.willReturn(true)

        // When
        val actualValue = classUnderTest.invoke()

        // Then
        assertEquals(expectedValue, actualValue)
    }

}

class SomeUseCase(
    private val repo: SomeRepository
) {

    operator fun invoke(): Boolean {
        return repo.getData()
    }

}

interface SomeRepository {
    fun getData(): Boolean
}