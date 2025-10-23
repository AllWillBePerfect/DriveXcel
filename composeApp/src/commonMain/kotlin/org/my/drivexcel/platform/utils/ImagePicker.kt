package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable

interface ImagePicker {

    @Composable
    fun PickImage(onImageSelected: (ByteArray) -> Unit)

    fun launchPicker()

}