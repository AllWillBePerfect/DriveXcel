package org.my.drivexcel.domain.model

data class LeaderUserDomainModel(
    val id: Int,
    val fullName: String,
    val age: Int,
    val company: String?,
    val jobTitle: String?,
    val role: String,
    val format: String,
    val blackMark: String,
    val dateOfVisit: String?,
    val applicationDate: String,
    val applicationStatus: String,
    val email: String,
    val phone: String,
    val city: String,
    val region: String,
    val placeOfStudy: String,
    val speciality: String?,
    val formOfStudy: String?,
    val studyFormat: String?,
    val educationLevel: String?
) {
    val tokens: List<String>

    init {
        tokens = normalize(fullName)
    }

    private fun normalize(input: String): List<String> {
        return input
            .lowercase()
            .replace("ё", "е")
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }

    companion object {
        fun generateUsers(count: Int = 100): List<LeaderUserDomainModel> {
            val firstNames = listOf(
                "Иван", "Петр", "Сергей", "Алексей", "Дмитрий",
                "Андрей", "Николай", "Максим", "Евгений", "Владимир"
            )

            val lastNames = listOf(
                "Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов",
                "Попов", "Васильев", "Новиков", "Федоров", "Морозов"
            )

            val middleNames = listOf(
                "Иванович", "Петрович", "Сергеевич", "Алексеевич", "Дмитриевич",
                "Андреевич", "Николаевич", "Максимович", "Евгеньевич", "Владимирович"
            )

            return List(count) { index ->
                val fullName =
                    "${lastNames.random()} ${firstNames.random()} ${middleNames.random()}"
                LeaderUserDomainModel(
                    id = index,
                    fullName = fullName,
                    age = (18..60).random(),
                    company = null,
                    jobTitle = null,
                    role = "Participant",
                    format = "Offline",
                    blackMark = "",
                    dateOfVisit = "01.01.2024",
                    applicationDate = "01.01.2024",
                    applicationStatus = "Pending",
                    email = "user$index@example.com",
                    phone = "+700000000$index",
                    city = "Moscow",
                    region = "Moscow Region",
                    placeOfStudy = "University",
                    speciality = null,
                    formOfStudy = null,
                    studyFormat = null,
                    educationLevel = null
                )
            }
        }
    }
}