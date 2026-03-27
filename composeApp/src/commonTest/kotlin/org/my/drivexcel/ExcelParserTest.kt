package org.my.drivexcel

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.my.drivexcel.data.v3.ExcelParser
import org.my.drivexcel.domain.models.Participant
import org.my.drivexcel.domain.models.ParticipantId
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/*
class ExcelParserTest {

    private lateinit var parser: ExcelParser

    @BeforeTest
    fun setup() {
        parser = ExcelParser.Impl()
    }

    @Test
    fun `write and parse returns identical participants`() = runTest {
        
        val participants = listOf(
            Participant(
                id = ParticipantId(1),
                fullName = "John Doe",
                age = 25,
                company = "ACME",
                jobTitle = "Engineer",
                role = "Guest",
                format = "Offline",
                blackMark = false,
                dateOfVisit = null,
                applicationDate = "2024-01-01",
                applicationStatus = "Approved",
                email = "john@test.com",
                phone = "123456",
                city = "Berlin",
                region = "BE",
                placeOfStudy = "TU Berlin",
                speciality = null,
                formOfStudy = null,
                studyFormat = null,
                educationLevel = null
            )
        )

        val bytes = parser.write(participants)
        val parsed = parser.parse(bytes)

        assertEquals(participants, parsed)
    }

    @Test
    fun `write and parse multiple participants`() = runTest {

        val participants = (1..5).map {
            Participant(
                id = ParticipantId(it),
                fullName = "User $it",
                age = 20 + it,
                company = null,
                jobTitle = null,
                role = "Role",
                format = "Online",
                blackMark = it % 2 == 0,
                dateOfVisit = null,
                applicationDate = "2024-01-01",
                applicationStatus = "Pending",
                email = "user$it@test.com",
                phone = "000$it",
                city = "City",
                region = "Region",
                placeOfStudy = "University",
                speciality = null,
                formOfStudy = null,
                studyFormat = null,
                educationLevel = null
            )
        }

        val bytes = parser.write(participants)
        val parsed = parser.parse(bytes)

        assertEquals(participants, parsed)
    }

    @Test
    fun `nullable fields are preserved`() = runTest {

        val participant = Participant(
            id = ParticipantId(1),
            fullName = "Test",
            age = 30,
            company = null,
            jobTitle = null,
            role = "Role",
            format = "Offline",
            blackMark = false,
            dateOfVisit = null,
            applicationDate = "2024-01-01",
            applicationStatus = "Approved",
            email = "test@test.com",
            phone = "123",
            city = "City",
            region = "Region",
            placeOfStudy = "Study",
            speciality = null,
            formOfStudy = null,
            studyFormat = null,
            educationLevel = null
        )

        val bytes = parser.write(listOf(participant))
        val parsed = parser.parse(bytes)

        assertEquals(participant, parsed.first())
    }

    @Test
    fun `parse empty excel returns empty list`() = runTest {

        val bytes = parser.write(emptyList())

        val parsed = parser.parse(bytes)

        assertTrue(parsed.isEmpty())
    }

    @Test
    fun `parse invalid bytes throws exception`() = runTest {

        assertFails {
            parser.parse("not excel".encodeToByteArray())
        }
    }






}*/
