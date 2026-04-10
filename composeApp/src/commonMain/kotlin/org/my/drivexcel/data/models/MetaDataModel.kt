package org.my.drivexcel.data.models

import kotlinx.serialization.Serializable
import org.my.drivexcel.domain.model.ImageExtension

@Serializable
data class MetaDataModel(
    val eventName: String,
    val imageExtension: ImageExtension?
)