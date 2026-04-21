package org.my.drivexcel.data.models

enum class DirTypeDataModel {
    LOCAL, TEST
}

fun DirTypeDataModel.toPath() = when (this) {
    DirTypeDataModel.LOCAL -> "localEvents"
    DirTypeDataModel.TEST -> "testLocalEvents"
}