package org.my.drivexcel.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LeaderUserDataModel(
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
)