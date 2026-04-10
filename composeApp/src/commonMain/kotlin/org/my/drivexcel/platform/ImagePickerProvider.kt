package org.my.drivexcel.platform

import androidx.compose.runtime.Composable

interface ImagePickerProvider {

    @Composable
    fun RegisterImagePicker(onImageSelected: (ByteArray) -> Unit)

    fun launchPicker()

}