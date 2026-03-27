package org.my.drivexcel.domain.models

data class Participant(
    val id: ParticipantId,
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
)

@JvmInline
value class ParticipantId(val value: Int)