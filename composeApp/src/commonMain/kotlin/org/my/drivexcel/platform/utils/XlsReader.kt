package org.my.drivexcel.platform.utils

interface XlsReader {
    fun readExcel(filePath: String): List<LeaderUser>
    fun writeExcel(filePath: String)
}

data class LeaderUser(
    val id: Int,
    val fullName: String,
    val age: Int,
    val company: String?,
    val jobTitle: String?,
    val role: String,
    val format: String,
    val blackMark: Boolean,
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
    val getFirstLetter: String = fullName.firstOrNull().toString()

    companion object {
        fun createDefault() = LeaderUser(
            id = 0,
            fullName = "Лидеров Лидер Лидерович",
            age = 0,
            company = null,
            jobTitle = null,
            role = "",
            format = "",
            blackMark = false,
            dateOfVisit = null,
            applicationDate = "",
            applicationStatus = "",
            email = "",
            phone = "",
            city = "",
            region = "",
            placeOfStudy = "",
            speciality = null,
            formOfStudy = null,
            studyFormat = null,
            educationLevel = null
        )
    }
}