package org.my.drivexcel.data.mappers

import org.my.drivexcel.data.models.LeaderUserDataModel
import org.my.drivexcel.domain.model.LeaderUserDomainModel

class LeaderUserDataToDomainMapper {

    fun toDomain(dataUser: LeaderUserDataModel): LeaderUserDomainModel =
        LeaderUserDomainModel(
            id = dataUser.id,
            fullName = dataUser.fullName,
            age = dataUser.age,
            company = dataUser.company,
            jobTitle = dataUser.jobTitle,
            role = dataUser.role,
            format = dataUser.format,
            blackMark = dataUser.blackMark,
            dateOfVisit = dataUser.dateOfVisit,
            applicationDate = dataUser.applicationDate,
            applicationStatus = dataUser.applicationStatus,
            email = dataUser.email,
            phone = dataUser.phone,
            city = dataUser.city,
            region = dataUser.region,
            placeOfStudy = dataUser.placeOfStudy,
            speciality = dataUser.speciality,
            formOfStudy = dataUser.formOfStudy,
            studyFormat = dataUser.studyFormat,
            educationLevel = dataUser.educationLevel
        )

    fun fromDomain(domain: LeaderUserDomainModel): LeaderUserDataModel =
        LeaderUserDataModel(
            id = domain.id,
            fullName = domain.fullName,
            age = domain.age,
            company = domain.company,
            jobTitle = domain.jobTitle,
            role = domain.role,
            format = domain.format,
            blackMark = domain.blackMark,
            dateOfVisit = domain.dateOfVisit,
            applicationDate = domain.applicationDate,
            applicationStatus = domain.applicationStatus,
            email = domain.email,
            phone = domain.phone,
            city = domain.city,
            region = domain.region,
            placeOfStudy = domain.placeOfStudy,
            speciality = domain.speciality,
            formOfStudy = domain.formOfStudy,
            studyFormat = domain.studyFormat,
            educationLevel = domain.educationLevel
        )

    fun toDomainList(dataList: List<LeaderUserDataModel>): List<LeaderUserDomainModel> =
        dataList.map { toDomain(it) }
}