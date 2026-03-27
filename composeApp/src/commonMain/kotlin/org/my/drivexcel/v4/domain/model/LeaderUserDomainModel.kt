package org.my.drivexcel.v4.domain.model

data class LeaderUserDomainModel(
    val id: Int,
    val fullName: String,
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
                    fullName = fullName
                )
            }
        }
    }
}