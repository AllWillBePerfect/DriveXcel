package org.my.drivexcel.v4.data.models

import kotlinx.serialization.Serializable
import org.my.drivexcel.domain.models.ImageExtension

@Serializable
data class MetaDataModel(
    val eventName: String,
    val imageExtension: ImageExtension?
)